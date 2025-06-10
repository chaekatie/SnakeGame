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
    @PostMapping("/forgot-password-request-otp")

    public ResponseEntity<String> forgotPasswordRequestOtp(@RequestBody OtpVerificationRequest request) {
        try {

            passwordResetService.generateAndSendOtp(request.getEmail());
            return ResponseEntity.ok("OTP has been sent to your email.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

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
