package com.mnu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mnu.entity.Course;
import com.mnu.entity.GraduationRequirement;
import com.mnu.entity.Student;
import com.mnu.entity.TakenCourse;
import com.mnu.repository.CourseRepository;
import com.mnu.repository.GraduationRequirementRepository;
import com.mnu.repository.NonCurricularProgramRepository;
import com.mnu.repository.StudentProgramRepository;
import com.mnu.repository.StudentRepository;
import com.mnu.repository.TakenCourseRepository;
import com.mnu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import com.mnu.entity.User;
import com.mnu.dto.CategoryCreditsDTO;
import com.mnu.dto.GraduationStatusDTO;
import com.mnu.dto.TakenCourseDTO;
import com.mnu.dto.GraduationStatusDTO;

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
    private final PdfParserService pdfParserService; // 추가 필요

    public void parseGradePdfAndStore(String username, MultipartFile pdfFile) {
        Map<String, Object> parsed = pdfParserService.parsePdf(pdfFile);
        List<Map<String, String>> courses = (List<Map<String, String>>) parsed.get("courses");

        // 1. 학생 조회 or 생성
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return;

        String studentId = username;

        Student student = studentRepo.findById(studentId)
            .orElseGet(() -> {
                // 기본값 생성 (email, major는 null 허용됨에 주의)
                Student newStudent = new Student();
                newStudent.setStudentId(studentId);
                newStudent.setPassword("default"); // 또는 userOpt.get().getPassword()
                newStudent.setMajor(userOpt.get().getMajor());
                newStudent.setEmail(userOpt.get().getEmail());
                newStudent.setAdmissionYear(2024); // 또는 파싱 값
                return studentRepo.save(newStudent);
            });


        // 2. 이수 과목 저장
        for (Map<String, String> course : courses) {
            String courseCode = course.get("courseCode");
            String grade = course.get("grade");
            String semester = course.get("semester");

            TakenCourse tc = new TakenCourse();
            tc.setStudentId(studentId);
            tc.setCourseCode(courseCode);
            if (courseCode != null && grade != null && !grade.isBlank()) {
                tc.setCompleted("Y");
            } else {
                tc.setCompleted("N"); // 혹은 continue;
            }
            tc.setGrade(grade);
            tc.setSemester(semester); // ✅ 추가

            System.out.println("저장 중: " + courseCode + " / 성적: " + grade + " / 학기: " + semester);
            takenCourseRepo.save(tc);
        }
    }
    public GraduationStatusDTO checkStatus(String studentId) {
        Student student = studentRepo.findById(studentId).orElseThrow();
        int year = student.getAdmissionYear();
        String major = student.getMajor();

        // 1. 영역별 이수 학점
        List<Object[]> creditSums = takenCourseRepo.sumCreditsByCategory(studentId);
        Map<String, Integer> creditMap = creditSums.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).intValue()
                ));

        // 2. 입학년도별 요구 학점
        List<GraduationRequirement> requirements = gradReqRepo.findByMajorAndAdmissionYear(major, year);
        Map<String, Integer> requiredMap = requirements.stream()
                .collect(Collectors.toMap(
                        GraduationRequirement::getCategory,
                        GraduationRequirement::getRequiredCredits
                ));

        // 3. 미이수 전공필수 과목
        List<Course> missingCourses = courseRepo.findUncompletedRequired(studentId);

        // 4. 비교과 프로그램 이수 현황
        List<Object[]> programStatus = nonCurricularRepo.checkProgramStatus(studentId);
        List<GraduationStatusDTO.NonCurricularStatus> programDTOs = new ArrayList<>();
        boolean allProgramsCompleted = true;

        for (Object[] row : programStatus) {
            String name = (String) row[0];
            String completed = (String) row[1];
            boolean isDone = "Y".equalsIgnoreCase(completed);
            if (!isDone) allProgramsCompleted = false;

            GraduationStatusDTO.NonCurricularStatus dto = new GraduationStatusDTO.NonCurricularStatus();
            dto.setProgramName(name);
            dto.setCompleted(isDone);
            programDTOs.add(dto);
        }

        // 5. 학기별 이수 과목
        List<TakenCourse> takenCourses = takenCourseRepo.findByStudentId(studentId);
        Map<String, List<TakenCourseDTO>> takenBySemester = new HashMap<>();

        for (TakenCourse tc : takenCourses) {
            Course course = courseRepo.findById(tc.getCourseCode()).orElse(null);
            if (course == null) continue;

            String semester = tc.getSemester() != null ? tc.getSemester() : "미분류";
            TakenCourseDTO dto = new TakenCourseDTO();
            dto.setCourseCode(tc.getCourseCode());
            dto.setCourseName(course.getCourseName());
            dto.setGrade(tc.getGrade());

            takenBySemester.computeIfAbsent(semester, k -> new ArrayList<>()).add(dto);
        }

        // 6. DTO 구성
        GraduationStatusDTO dto = new GraduationStatusDTO();
        dto.setGeneralEducationCredits(creditMap.getOrDefault("전문교양", 0));
        dto.setEngineeringBasicsCredits(creditMap.getOrDefault("공학기초", 0));
        dto.setMajorRequiredCredits(creditMap.getOrDefault("전공필수", 0));
        dto.setMajorElectiveCredits(creditMap.getOrDefault("전공선택", 0));

        dto.setRequiredGeneralEducationCredits(requiredMap.getOrDefault("전문교양", 0));
        dto.setRequiredEngineeringBasicsCredits(requiredMap.getOrDefault("공학기초", 0));
        dto.setRequiredMajorRequiredCredits(requiredMap.getOrDefault("전공필수", 0));
        dto.setRequiredMajorElectiveCredits(requiredMap.getOrDefault("전공선택", 0));

        // 미이수 전공필수 과목
        List<GraduationStatusDTO.CourseDTO> missingList = missingCourses.stream()
                .map(c -> {
                    GraduationStatusDTO.CourseDTO cdto = new GraduationStatusDTO.CourseDTO();
                    cdto.setCourseCode(c.getCourseCode());
                    cdto.setCourseName(c.getCourseName());
                    return cdto;
                })
                .collect(Collectors.toList());

        dto.setMissingRequiredCourses(missingList);
        dto.setNonCurricularPrograms(programDTOs);
        dto.setNonCurricularOk(allProgramsCompleted);
        dto.setTakenBySemester(takenBySemester);

        return dto;
    }
    
}
