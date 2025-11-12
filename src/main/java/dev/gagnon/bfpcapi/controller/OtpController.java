package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.service.OTPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@Slf4j
@RequiredArgsConstructor
public class OtpController {
    
    private final OTPService otpService;

    @GetMapping("/generate")
    public Map<String, String> generateTestOTP() {
        String otp = otpService.generateOTP();
        log.info("Generated test OTP: {}", otp);
        return Map.of("otp", otp, "message", "Test OTP generated successfully");
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "OK", "message", "OTP service is running");
    }
}