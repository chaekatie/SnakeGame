package com.snakegame.backend.service;

import com.snakegame.backend.model.PasswordResetToken;

import com.snakegame.backend.model.User;
import com.snakegame.backend.repository.PasswordResetTokenRepository;
import com.snakegame.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void sendResetLink(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email not found."));

        // Xoá token cũ nếu tồn tại
        tokenRepository.deleteByUser(user);

        // Tạo token mới
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationTime(LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(resetToken);

        // Gửi email chứa link
        String link = "http://localhost:8080/reset-password-form?token=" + token;
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Yêu cầu đặt lại mật khẩu");
        mail.setText("Vui lòng nhấn vào liên kết sau để đặt lại mật khẩu:\n" + link);

        mailSender.send(mail);
    }


    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid Token."));

        if (resetToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}
