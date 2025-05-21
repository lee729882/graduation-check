package com.mnu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mnu.entity.Course;
import com.mnu.entity.Student;
import com.mnu.repository.CourseRepository;
import com.mnu.repository.GraduationRequirementRepository;
import com.mnu.repository.NonCurricularProgramRepository;
import com.mnu.repository.StudentProgramRepository;
import com.mnu.repository.StudentRepository;
import com.mnu.repository.TakenCourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GraduationCheckService {

    private final TakenCourseRepository takenCourseRepo;
    private final CourseRepository courseRepo;
    private final GraduationRequirementRepository gradReqRepo;
    private final StudentProgramRepository studentProgramRepo;
    private final NonCurricularProgramRepository nonCurricularRepo;
    private final StudentRepository studentRepo;

    public Map<String, Object> checkStatus(String studentId) {
        Map<String, Object> result = new HashMap<>();
        Student student = studentRepo.findById(studentId).orElseThrow();
        int year = student.getAdmissionYear();
        String major = student.getMajor();

        // ① 영역별 학점 합산
        List<Object[]> creditSums = takenCourseRepo.sumCreditsByCategory(studentId);
        result.put("credits", creditSums);

        // ② 필수 과목 미이수
        List<Course> uncompletedRequired = courseRepo.findUncompletedRequired(studentId);
        result.put("missingRequiredCourses", uncompletedRequired);

        // ③ 비교과 이수 상태
        List<Object[]> programStatus = nonCurricularRepo.checkProgramStatus(studentId);
        result.put("nonCurricular", programStatus);

        return result;
    }
}
