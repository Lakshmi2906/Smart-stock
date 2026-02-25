package com.smartstock.repository;

import com.smartstock.model.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Integer> {

    @Query("SELECT o FROM OtpCode o WHERE o.phone = :phone AND o.used = false AND o.expiresAt > :now ORDER BY o.createdAt DESC")
    Optional<OtpCode> findLatestValidOtp(@Param("phone") String phone, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM OtpCode o WHERE o.expiresAt < :before")
    int deleteExpiredOtps(@Param("before") LocalDateTime before);
}
