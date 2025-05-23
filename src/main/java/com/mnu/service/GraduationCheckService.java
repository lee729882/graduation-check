package com.mnu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mnu.entity.Course;
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
import com.mnu.dto.TakenCourseDTO;

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

    public Map<String, Object> checkStatus(String studentId) {
        Map<String, Object> result = new HashMap<>();
        Student student = studentRepo.findById(studentId).orElseThrow();
        
        List<TakenCourse> takenCourses = takenCourseRepo.findByStudentId(studentId);
        System.out.println("조회된 TakenCourse 수: " + takenCourses.size());

        List<Object[]> creditSums = takenCourseRepo.sumCreditsByCategory(studentId);
        List<CategoryCreditsDTO> creditDtoList = creditSums.stream()
            .map(row -> new CategoryCreditsDTO((String) row[0], ((Number) row[1]).intValue()))
            .collect(Collectors.toList());        
        System.out.println("조회된 학점 합계: " + creditSums.size());
        
        int year = student.getAdmissionYear();
        String major = student.getMajor();

        // ① 영역별 학점 합산
        result.put("credits", creditDtoList);  
        // ② 필수 과목 미이수
        List<Course> uncompletedRequired = courseRepo.findUncompletedRequired(studentId);
        result.put("missingRequiredCourses", uncompletedRequired);

        // ③ 비교과 이수 상태
        List<Object[]> programStatus = nonCurricularRepo.checkProgramStatus(studentId);
        result.put("nonCurricular", programStatus);

        // ④ 학기별 이수 과목
        Map<String, List<TakenCourseDTO>> takenBySemester = new HashMap<>();

        for (TakenCourse tc : takenCourses) {
            Course course = courseRepo.findById(tc.getCourseCode()).orElse(null);
            if (course == null) continue;

            String semester = tc.getSemester();
            if (semester == null) semester = "미분류";

            TakenCourseDTO dto = new TakenCourseDTO();
            dto.setCourseCode(tc.getCourseCode());
            dto.setCourseName(course.getCourseName());
            dto.setGrade(tc.getGrade());
            dto.setSemester(tc.getSemester()); // 이 코드가 정상적으로 실행되고 있는지 확인

            takenBySemester.computeIfAbsent(semester, k -> new ArrayList<>()).add(dto);
        }
        System.out.println("========== 학기별 이수 과목 ==========");
        for (String sem : takenBySemester.keySet()) {
            System.out.println("학기: " + sem);
            for (TakenCourseDTO dto : takenBySemester.get(sem)) {
                System.out.println(" - " + dto.getCourseCode() + " / " + dto.getCourseName() + " / " + dto.getGrade());
            }
        }

        result.put("takenBySemester", takenBySemester);

        return result;
    }
    
}
