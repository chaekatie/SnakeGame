package com.snakegame.backend.service;

import com.snakegame.backend.model.OtpVerification;
import com.snakegame.backend.model.User;
import com.snakegame.backend.repository.OtpVerificationRepository;
import com.snakegame.backend.repository.UserRepository;
import jakarta.transaction.Transactional; // Đảm bảo import đúng
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpVerificationRepository otpVerificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 5;

    @Transactional // Rất quan trọng để phương thức này chạy trong một transaction
    public void generateAndSendOtp(String email) {
        // Sửa đổi dòng này để sử dụng Optional<User> và xử lý trường hợp không tìm thấy.
        // .orElseThrow() sẽ ném RuntimeException nếu không tìm thấy User.
        // Điều này cũng sẽ xử lý lỗi NonUniqueResultException nếu UserRepository trả về nhiều hơn 1 user
        // (vì findByEmail() với Optional chỉ mong đợi 0 hoặc 1).
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email not found."));

        // **Bước Cải thiện: Xoá tất cả các OTP cũ của người dùng này**
        // Điều này đảm bảo rằng mỗi người dùng chỉ có một OTP hợp lệ tại một thời điểm.
        // (Đảm bảo phương thức deleteByUser(user) hoặc deleteAllByUser(user) tồn tại trong OtpVerificationRepository và hoạt động đúng).
        otpVerificationRepository.deleteAllByUser(user); // Sửa lỗi Cannot resolve symbol 'user' bằng cách khai báo user ở trên

        // Tạo OTP mới
        String otpCode = generateOtp();
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setOtpCode(otpCode);
        otpVerification.setUser(user);
        otpVerification.setExpirationTime(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));

        try {
            otpVerificationRepository.save(otpVerification); // Lưu OTP mới
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Xử lý trường hợp hiếm gặp khi vẫn có lỗi trùng lặp (có thể do concurrency rất cao)
            // Trong trường hợp này, bạn có thể thử lại việc tạo OTP hoặc log lỗi cụ thể
            System.err.println("Failed to save OTP due to data integrity violation: " + e.getMessage());
            throw new RuntimeException("Could not generate OTP. Please try again.", e);
        }

        // Gửi email chứa OTP
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Mã xác thực đặt lại mật khẩu của bạn");
        mail.setText("Mã xác thực của bạn là: " + otpCode + "\nMã này sẽ hết hạn sau " + OTP_EXPIRATION_MINUTES + " phút.");

        mailSender.send(mail);
    }

    @Transactional
    public void verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email not found."));

        OtpVerification otpVerification = otpVerificationRepository.findByUserAndOtpCode(user, otp)
            .orElseThrow(() -> new RuntimeException("Invalid OTP or email."));

        if (otpVerification.getExpirationTime().isBefore(LocalDateTime.now())) {
            // **Khi OTP hết hạn, xoá nó đi**
            otpVerificationRepository.delete(otpVerification);
            throw new RuntimeException("OTP expired. Please request a new one.");
        }
        // Ở đây không xóa OTP, vì nó sẽ được xóa khi mật khẩu được reset thành công
    }

    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email not found."));

        // Tìm và xóa tất cả OTP liên quan đến người dùng này sau khi mật khẩu được thay đổi
        // Điều này đảm bảo rằng OTP đã sử dụng không thể bị sử dụng lại
        otpVerificationRepository.deleteAllByUser(user); // Hoặc deleteByUser(user);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Sau khi đặt lại mật khẩu thành công, tất cả OTP liên quan đến người dùng này đã được xóa ở trên.
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a 6-digit OTP
        return String.valueOf(otp);
    }
}
