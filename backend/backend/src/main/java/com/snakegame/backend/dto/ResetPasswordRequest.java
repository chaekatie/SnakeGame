package com.snakegame.backend.dto;

public class ResetPasswordRequest {
    private String email; // Email của người dùng để xác định tài khoản
    private String newPassword; // Mật khẩu mới

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
