package com.snakegame.backend.dto;

public class OtpVerificationRequest {
    private String email; // Email của người dùng
    private String otp;   // Mã OTP mà người dùng nhập

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
