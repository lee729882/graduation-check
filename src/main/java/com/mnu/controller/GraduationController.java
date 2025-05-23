package com.mnu.controller;

import com.mnu.service.GraduationCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/graduation")
public class GraduationController {

    private final GraduationCheckService graduationCheckService;

    // HTML 페이지 반환
    @GetMapping("/status/{studentId}")
    public String getStatus(@PathVariable String studentId, Model model) {
        Map<String, Object> result = graduationCheckService.checkStatus(studentId);

        System.out.println("==== Controller에서 받은 result ====");
        result.forEach((k, v) -> System.out.println(k + " : " + v));

        model.addAttribute("credits", result.get("credits"));
        model.addAttribute("missingRequiredCourses", result.get("missingRequiredCourses"));
        model.addAttribute("nonCurricular", result.get("nonCurricular"));
        model.addAttribute("takenBySemester", result.get("takenBySemester"));

        return "graduation-status";
    }


    // JSON API 응답
    @GetMapping("/status-json/{studentId}")
    public ResponseEntity<Map<String, Object>> getStatusJson(@PathVariable String studentId) {
        Map<String, Object> result = graduationCheckService.checkStatus(studentId);
        return ResponseEntity.ok(result);
    }
    
}
