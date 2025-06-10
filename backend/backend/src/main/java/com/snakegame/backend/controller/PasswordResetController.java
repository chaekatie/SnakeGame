package com.snakegame.backend.controller;

import com.snakegame.backend.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.snakegame.backend.dto.OtpVerificationRequest; // Đảm bảo dòng này đã có
import com.snakegame.backend.dto.ResetPasswordRequest;

@RestController
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;

    // Endpoint để yêu cầu gửi OTP
    @PostMapping("/forgot-password-request-otp")
    // THAY ĐỔI: Từ @RequestParam String email sang @RequestBody OtpVerificationRequest request
    public ResponseEntity<String> forgotPasswordRequestOtp(@RequestBody OtpVerificationRequest request) { // <-- Dòng này đã thay đổi
        try {
            // Lấy email từ request object. Trường otp sẽ là null nhưng không ảnh hưởng.
            passwordResetService.generateAndSendOtp(request.getEmail()); // <-- Dòng này đã thay đổi
            return ResponseEntity.ok("OTP has been sent to your email.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Endpoint để xác thực OTP (Giữ nguyên, vẫn dùng OtpVerificationRequest)
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest request) {
        try {
            passwordResetService.verifyOtp(request.getEmail(), request.getOtp());
            return ResponseEntity.ok("OTP verified successfully. You can now reset your password.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Endpoint để đặt lại mật khẩu sau khi OTP đã được xác thực (Giữ nguyên)
    @PostMapping("/reset-password-with-otp")
    public ResponseEntity<String> resetPasswordWithOtp(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
