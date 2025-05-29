package com.snakegame.backend.controller;

import com.snakegame.backend.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.snakegame.backend.dto.ResetPasswordRequest;

@RestController
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            passwordResetService.sendResetLink(email);
            return ResponseEntity.ok("Link đặt lại mật khẩu đã được gửi đến email của bạn.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Mật khẩu đã được cập nhật thành công.");
    }

    @GetMapping("/reset-password-form")
    @ResponseBody
    public String showResetForm(@RequestParam("token") String token) {
        return "Đã nhận được token: " + token;
    }

}
