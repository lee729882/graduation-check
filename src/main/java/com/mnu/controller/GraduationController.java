package com.mnu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mnu.service.GraduationCheckService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@Controller
@RequestMapping("/graduation")
public class GraduationController {

    private final GraduationCheckService checkService;

    public GraduationController(GraduationCheckService checkService) {
        this.checkService = checkService;
    }

    @GetMapping("/status/{studentId}")
    public String redirectToStaticPage() {
        return "redirect:/graduation-status.html";
    }

}
