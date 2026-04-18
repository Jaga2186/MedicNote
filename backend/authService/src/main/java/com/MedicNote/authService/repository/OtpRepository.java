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

    Optional<OtpRecord> findBySessionToken(String sessionToken);

    @Modifying
    @Transactional
    @Query("DELETE FROM OtpRecord o WHERE o.sessionExpiresAt < :now OR o.isUsed = true")
    void deleteExpiredAndUsedOtps(LocalDateTime now);
}
