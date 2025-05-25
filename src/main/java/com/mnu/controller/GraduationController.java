package com.mnu.controller;

import com.mnu.service.GraduationCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.mnu.dto.GraduationStatusDTO;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/graduation")
public class GraduationController {

    private final GraduationCheckService graduationCheckService;

    // HTML 페이지 반환
    @GetMapping("/status/{studentId}")
    public String getStatus(@PathVariable String studentId, Model model) {
        GraduationStatusDTO status = graduationCheckService.checkStatus(studentId);

        // (선택) 디버깅 로그
        System.out.println("== GraduationStatusDTO ==");
        System.out.println("전공필수 이수학점: " + status.getMajorRequiredCredits());
        System.out.println("미이수 전공필수 과목 수: " + status.getMissingRequiredCourses().size());

        // 한 번에 통째로 넘기기
        model.addAttribute("status", status);

        return "graduation-status";
    }


    // JSON API 응답
    @GetMapping("/status-json/{studentId}")
    public ResponseEntity<GraduationStatusDTO> getStatusJson(@PathVariable String studentId) {
        GraduationStatusDTO status = graduationCheckService.checkStatus(studentId);
        return ResponseEntity.ok(status);
    }

    
}
