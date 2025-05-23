package com.mnu.controller;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mnu.entity.User;
import com.mnu.repository.UserRepository;
import com.mnu.service.GraduationCheckService;
import com.mnu.service.MailService;

import com.mnu.entity.Student;
import com.mnu.repository.StudentRepository;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final GraduationCheckService graduationCheckService;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder; // 👉 추가


    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam("major") String major,
            @RequestParam("admissionYear") int admissionYear,
            @RequestParam("gradePdf") MultipartFile pdfFile) {

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미 사용 중인 아이디입니다."));
        }

        String encodedPassword = passwordEncoder.encode(password);

        // User 저장
        User user = User.builder()
                .username(username)
                .password(encodedPassword)  // ✅ 암호화 적용
                .email(email)
                .studentId(username)
                .major(major)
                .build();
        userRepository.save(user);

        // Student 저장
        Student student = new Student();
        student.setStudentId(username);
        student.setPassword(encodedPassword);
        student.setEmail(email);
        student.setMajor(major);
        student.setAdmissionYear(admissionYear);
        student.setName("이름없음");
        studentRepository.save(student);

        // PDF 분석
        graduationCheckService.parseGradePdfAndStore(username, pdfFile);

        return ResponseEntity.ok(Map.of(
            "message", "회원가입 완료 및 성적 분석 완료",
            "redirectUrl", "/login"
        ));
    }



    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginUser) {
        Optional<User> user = userRepository.findByUsername(loginUser.getUsername());

        if (user.isEmpty() || !passwordEncoder.matches(loginUser.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return ResponseEntity.ok("/graduation/status/" + loginUser.getUsername());
    }


    
    @PostMapping("/find-id")
    public ResponseEntity<String> findId(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            mailService.sendEmail(email, "아이디 찾기", "회원님의 아이디는: " + user.getUsername());
            return ResponseEntity.ok("입력하신 이메일로 아이디를 전송했습니다.");
        } else {
            return ResponseEntity.badRequest().body("해당 이메일로 등록된 아이디가 없습니다.");
        }
    }


    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");

        Optional<User> userOpt = userRepository.findByUsernameAndEmail(username, email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 임시 비밀번호 생성
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);

            // 비밀번호 변경 후 저장
            user.setPassword(passwordEncoder.encode(tempPassword));
            userRepository.save(user);

            // 이메일 발송
            mailService.sendEmail(email, "임시 비밀번호 안내", "임시 비밀번호: " + tempPassword);

            return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("일치하는 회원이 없습니다.");
        }
    }



}