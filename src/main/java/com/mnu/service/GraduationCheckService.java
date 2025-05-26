package com.mnu.service;

import com.mnu.dto.GraduationStatusDTO;
import com.mnu.dto.TakenCourseDTO;
import com.mnu.entity.*;
import com.mnu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GraduationCheckService {

    private final UserRepository userRepository;
    private final TakenCourseRepository takenCourseRepo;
    private final CourseRepository courseRepo;
    private final GraduationRequirementRepository gradReqRepo;
    private final StudentProgramRepository studentProgramRepo;
    private final NonCurricularProgramRepository nonCurricularRepo;
    private final StudentRepository studentRepo;
    private final PdfParserService pdfParserService;

    
    public void parseGradePdfAndStore(String username, MultipartFile pdfFile) {
        Map<String, Object> parsed = pdfParserService.parsePdf(pdfFile);
        List<Map<String, String>> courses = (List<Map<String, String>>) parsed.get("courses");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return;

        String studentId = username;

        Student student = studentRepo.findById(studentId).orElseGet(() -> {
            Student newStudent = new Student();
            newStudent.setStudentId(studentId);
            newStudent.setPassword("default");
            newStudent.setMajor(userOpt.get().getMajor());
            newStudent.setEmail(userOpt.get().getEmail());
            newStudent.setAdmissionYear(2024); // TODO: PDF에서 자동 추출 예정
            return studentRepo.save(newStudent);
        });

        for (Map<String, String> course : courses) {
            String courseCode = course.get("courseCode");
            String grade = course.get("grade");
            String semester = course.get("semester");

            TakenCourse tc = new TakenCourse();
            tc.setStudentId(studentId);
            tc.setCourseCode(courseCode);
            tc.setGrade(grade);
            tc.setSemester(semester);
            tc.setCompleted((courseCode != null && grade != null && !grade.isBlank()) ? "Y" : "N");

            takenCourseRepo.save(tc);
        }
    }

    public GraduationStatusDTO checkStatus(String studentId) {
        Student student = studentRepo.findById(studentId).orElseThrow();
        int year = student.getAdmissionYear();
        String major = student.getMajor();
        GraduationStatusDTO statusDto = new GraduationStatusDTO();

        // [1] 학점 취득 현황
        List<Object[]> creditSums = takenCourseRepo.sumCreditsByCategory(studentId);
        Map<String, Integer> creditMap = creditSums.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).intValue()
                ));
        
        statusDto.setEtcCredits(creditMap.getOrDefault("기타", 0));

        // [2] 졸업 요건 (입학년도 기준)
        List<GraduationRequirement> requirements = gradReqRepo.findByMajorAndAdmissionYear(major, year);
        Map<String, Integer> requiredMap = requirements.stream()
                .collect(Collectors.toMap(
                        GraduationRequirement::getCategory,
                        GraduationRequirement::getRequiredCredits
                ));
        statusDto.setRequiredEtcCredits(requiredMap.getOrDefault("기타", 0)); // ✅ 여기로 옮기기

        // [3] 전공필수 미이수 과목
        List<Course> missingCourses = courseRepo.findUncompletedRequired(studentId);
        List<GraduationStatusDTO.CourseDTO> missingList = missingCourses.stream()
                .map(c -> {
                    GraduationStatusDTO.CourseDTO cdto = new GraduationStatusDTO.CourseDTO();
                    cdto.setCourseCode(c.getCourseCode());
                    cdto.setCourseName(c.getCourseName());
                    return cdto;
                })
                .collect(Collectors.toList());
        // [3-1] 전문교양 미이수 과목
        List<Course> missingGeneralEducation = courseRepo.findUncompletedGeneralEducation(studentId);
        List<GraduationStatusDTO.CourseDTO> missingGenList = missingGeneralEducation.stream()
                .map(c -> {
                    GraduationStatusDTO.CourseDTO dto = new GraduationStatusDTO.CourseDTO();
                    dto.setCourseCode(c.getCourseCode());
                    dto.setCourseName(c.getCourseName());
                    return dto;
                })
                .collect(Collectors.toList());

        statusDto.setMissingGeneralEducationCourses(missingGenList);

        
        // [4] 비교과 이수 현황
        List<Object[]> programStatus = nonCurricularRepo.checkProgramStatus(studentId);
        List<GraduationStatusDTO.NonCurricularStatus> programDTOs = new ArrayList<>();
        boolean allProgramsCompleted = true;
        for (Object[] row : programStatus) {
            String name = (String) row[0];
            String completed = (String) row[1];
            boolean isDone = "Y".equalsIgnoreCase(completed);
            if (!isDone) allProgramsCompleted = false;

            GraduationStatusDTO.NonCurricularStatus programDto = new GraduationStatusDTO.NonCurricularStatus();
            programDto.setProgramName(name);
            programDto.setCompleted(isDone);
            programDTOs.add(programDto);
        }

        // [5] 학기별 이수 과목
        List<TakenCourse> takenCourses = takenCourseRepo.findByStudentId(studentId);
        Map<String, List<TakenCourseDTO>> takenBySemester = new LinkedHashMap<>();

	     // 먼저 학기 문자열 정렬을 위해 TreeSet 사용
	     Set<String> sortedSemesterSet = takenCourses.stream()
	         .map(tc -> tc.getSemester() == null ? "미분류" : tc.getSemester())
	         .distinct()
	         .sorted(Comparator.comparing(s -> s.replace("학기", "").replace("/", ".")
	             .replace("1", ".0").replace("2", ".5"))) // 예: 2024/1학기 → 2024.0
	         .collect(Collectors.toCollection(LinkedHashSet::new));
	
	     for (String sem : sortedSemesterSet) {
	         takenBySemester.put(sem, new ArrayList<>());
	     }
	
	     for (TakenCourse tc : takenCourses) {
	         Course course = courseRepo.findById(tc.getCourseCode()).orElse(null);
	         if (course == null) continue;
	
	         String semester = tc.getSemester() != null ? tc.getSemester() : "미분류";
	         TakenCourseDTO dto = new TakenCourseDTO();
	         dto.setCourseCode(tc.getCourseCode());
	         dto.setCourseName(course.getCourseName());
	         dto.setGrade(tc.getGrade());
	
	         takenBySemester.get(semester).add(dto);
	     }


        // [6] 평균 평점 계산
        double totalScore = 0;
        int subjectCount = 0;
        for (TakenCourse tc : takenCourses) {
            if (!"Y".equals(tc.getCompleted())) continue;
            String grade = tc.getGrade();
            if (grade == null || grade.isBlank()) continue;

            switch (grade) {
            case "A+":
                totalScore += 4.5; break;
            case "A0":
                totalScore += 4.0; break;
            case "B+":
                totalScore += 3.5; break;
            case "B0":
                totalScore += 3.0; break;
            case "C+":
                totalScore += 2.5; break;
            case "C0":
                totalScore += 2.0; break;
            case "D+":
                totalScore += 1.5; break;
            case "D0":
                totalScore += 1.0; break;
            case "F":
                totalScore += 0.0; break;
            default:
                continue; 
        }
        subjectCount++;

        }
        double avgGrade = subjectCount == 0 ? 0.0 : totalScore / subjectCount;

        // [7] DTO 구성
        statusDto.setStudentId(studentId);
        statusDto.setMajor(major);
        statusDto.setAdmissionYear(year);
        statusDto.setAverageGrade(Math.round(avgGrade * 100.0) / 100.0);

        statusDto.setGeneralEducationCredits(creditMap.getOrDefault("전문교양", 0));
        statusDto.setEngineeringBasicsCredits(creditMap.getOrDefault("공학기초", 0));
        statusDto.setMajorRequiredCredits(creditMap.getOrDefault("전공필수", 0));
        statusDto.setMajorElectiveCredits(creditMap.getOrDefault("전공선택", 0));

        statusDto.setRequiredGeneralEducationCredits(requiredMap.getOrDefault("전문교양", 0));
        statusDto.setRequiredEngineeringBasicsCredits(requiredMap.getOrDefault("공학기초", 0));
        statusDto.setRequiredMajorRequiredCredits(requiredMap.getOrDefault("전공필수", 0));
        statusDto.setRequiredMajorElectiveCredits(requiredMap.getOrDefault("전공선택", 0));

        statusDto.setMissingRequiredCourses(missingList);
        statusDto.setNonCurricularPrograms(programDTOs);
        statusDto.setNonCurricularOk(allProgramsCompleted);
        statusDto.setTakenBySemester(takenBySemester);

        // [8] 졸업 가능 여부 판정
        boolean graduationOk =
                statusDto.getGeneralEducationCredits() >= statusDto.getRequiredGeneralEducationCredits() &&
                statusDto.getEngineeringBasicsCredits() >= statusDto.getRequiredEngineeringBasicsCredits() &&
                statusDto.getMajorRequiredCredits() >= statusDto.getRequiredMajorRequiredCredits() &&
                statusDto.getMajorElectiveCredits() >= statusDto.getRequiredMajorElectiveCredits() &&
                statusDto.getEtcCredits() >= statusDto.getRequiredEtcCredits() && 

                statusDto.isNonCurricularOk() &&
                (statusDto.getMissingRequiredCourses() == null || statusDto.getMissingRequiredCourses().isEmpty());
        statusDto.setGraduationAvailable(graduationOk);

        // [9] 졸업 불가 사유 수집
        List<String> failReasons = new ArrayList<>();
        if (statusDto.getGeneralEducationCredits() < statusDto.getRequiredGeneralEducationCredits())
            failReasons.add("전문교양 학점 부족");
        if (statusDto.getEngineeringBasicsCredits() < statusDto.getRequiredEngineeringBasicsCredits())
            failReasons.add("공학기초 학점 부족");
        if (statusDto.getMajorRequiredCredits() < statusDto.getRequiredMajorRequiredCredits())
            failReasons.add("전공필수 학점 부족");
        if (statusDto.getMajorElectiveCredits() < statusDto.getRequiredMajorElectiveCredits())
            failReasons.add("전공선택 학점 부족");
        if (!statusDto.isNonCurricularOk())
            failReasons.add("비교과 프로그램 미이수");
        if (statusDto.getMissingRequiredCourses() != null && !statusDto.getMissingRequiredCourses().isEmpty())
            failReasons.add("전공필수 과목 일부 미이수");
        if (statusDto.getMissingGeneralEducationCourses() != null && !statusDto.getMissingGeneralEducationCourses().isEmpty())
            failReasons.add("전문교양 과목 일부 미이수");

        statusDto.setGraduationFailReasons(failReasons);

        
        // [10] 총 졸업 학점 및 취득 학점 요약
        int totalRequired = statusDto.getRequiredGeneralEducationCredits()
                + statusDto.getRequiredEngineeringBasicsCredits()
                + statusDto.getRequiredMajorRequiredCredits()
                + statusDto.getRequiredMajorElectiveCredits()
                + statusDto.getRequiredEtcCredits(); 

        int totalEarned = statusDto.getGeneralEducationCredits()
                + statusDto.getEngineeringBasicsCredits()
                + statusDto.getMajorRequiredCredits()
                + statusDto.getMajorElectiveCredits()
        		+ statusDto.getEtcCredits();  // 추가 ✅

        statusDto.setTotalRequiredCredits(totalRequired);
        statusDto.setTotalEarnedCredits(totalEarned);
        statusDto.setTotalCreditsOk(totalEarned >= totalRequired);


        
        return statusDto;
        
    }
}
