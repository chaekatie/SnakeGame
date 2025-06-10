package com.snakegame.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class OtpVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String otpCode; // Đổi từ 'token' thành 'otpCode' cho rõ ràng
    private LocalDateTime expirationTime;

    @OneToOne
    private User user;

    // Có thể thêm mục đích của OTP nếu cần (ví dụ: password_reset, registration_verify)
    // @Enumerated(EnumType.STRING)
    // private OtpPurpose otpPurpose;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}
