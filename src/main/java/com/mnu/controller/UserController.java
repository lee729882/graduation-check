package com.mnu.controller;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mnu.entity.User;
import com.mnu.repository.UserRepository;
import com.mnu.service.MailService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private final MailService mailService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("이미 사용 중인 아이디입니다.");
        }

        userRepository.save(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginUser) {
        Optional<User> user = userRepository.findByUsername(loginUser.getUsername());

        if (user.isEmpty() || !user.get().getPassword().equals(loginUser.getPassword())) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return ResponseEntity.ok("/graduation-status.html");  // 정적 파일 경로 반환
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
            user.setPassword(tempPassword); // 실제론 암호화 필요 (예: BCrypt)
            userRepository.save(user);

            // 이메일 발송
            mailService.sendEmail(email, "임시 비밀번호 안내", "임시 비밀번호: " + tempPassword);

            return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("일치하는 회원이 없습니다.");
        }
    }



}