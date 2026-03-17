package com.smartstock.controller;

import com.smartstock.service.OtpService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest req) {
        try {
            otpService.sendOtp(req.getPhone());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "OTP sent successfully to " + req.getPhone()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest req) {
        try {
            String token = otpService.verifyOtpAndGetToken(req.getPhone(), req.getOtp());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", token,
                    "phone", req.getPhone()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @Data static class OtpRequest { @NotBlank private String phone; }
    @Data static class OtpVerifyRequest { @NotBlank private String phone; @NotBlank private String otp; }
}
