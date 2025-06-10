package com.snakegame.backend.repository;

import com.snakegame.backend.model.OtpVerification;
import com.snakegame.backend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByOtpCode(String otpCode);
    @Transactional
    @Modifying
    void deleteByUser(User user);

    Optional<OtpVerification> findByUserAndOtpCode(User user, String otpCode);

    @Transactional
    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.user = :user")
    void deleteAllByUser(@Param("user") User user);
}
