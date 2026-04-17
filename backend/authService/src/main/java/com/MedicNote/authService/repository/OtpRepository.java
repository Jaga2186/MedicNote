package com.MedicNote.authService.repository;

import com.MedicNote.authService.entity.OtpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpRecord, Long> {

    // Get latest valid (unused, not expired) OTP for an email
    Optional<OtpRecord> findTopByEmailAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String email, LocalDateTime now);

    // Cleanup expired OTPs — scheduled job will call this
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpRecord o WHERE o.expiresAt < :now OR o.isUsed = true")
    void deleteExpiredAndUsedOtps(LocalDateTime now);
}
