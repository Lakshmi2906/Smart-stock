package com.smartstock.service;

import com.smartstock.config.JwtUtil;
import com.smartstock.model.OtpCode;
import com.smartstock.repository.OtpCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final JwtUtil jwtUtil;

    @Value("${otp.test.mode}")
    private boolean testMode;

    @Value("${otp.test.fixed}")
    private String testOtp;

    @Value("${otp.expiry.minutes}")
    private int expiryMinutes;

    // Admin phones — in production, load from DB
    private static final java.util.Set<String> ADMIN_PHONES = java.util.Set.of("9999999999");

    @Transactional
    public void sendOtp(String phone) {
        validatePhone(phone);

        String code = testMode ? testOtp : String.format("%06d", new Random().nextInt(999999));

        // Invalidate any previous unused OTPs for this phone
        otpCodeRepository.deleteExpiredOtps(LocalDateTime.now());

        OtpCode otpCode = OtpCode.builder()
                .phone(phone)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(expiryMinutes))
                .used(false)
                .build();

        otpCodeRepository.save(otpCode);

        if (testMode) {
            log.info("[TEST MODE] OTP for {}: {}", phone, code);
        } else {
            // In production: integrate Twilio/MSG91 here
            // twilioService.sendSms(phone, "Your SmartStock OTP: " + code);
            log.info("OTP sent to {} (SMS provider integration needed)", phone);
        }
    }

    @Transactional
    public String verifyOtpAndGetToken(String phone, String inputOtp) {
        validatePhone(phone);

        OtpCode otpCode = otpCodeRepository
                .findLatestValidOtp(phone, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("OTP expired or not found. Please request a new OTP."));

        if (!otpCode.getCode().equals(inputOtp)) {
            throw new RuntimeException("Invalid OTP. Please try again.");
        }

        otpCode.setUsed(true);
        otpCodeRepository.save(otpCode);

        String role = ADMIN_PHONES.contains(phone) ? "ADMIN" : "USER";
        return jwtUtil.generateToken(phone, role);
    }

    private void validatePhone(String phone) {
        if (phone == null || !phone.matches("^[6-9][0-9]{9}$")) {
            throw new IllegalArgumentException("Invalid Indian mobile number: " + phone);
        }
    }
}
