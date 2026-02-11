package com.secureapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.secureapp.model.*;
import com.secureapp.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final OTPService otpService;
    private final EmailService emailService;

    public UserService(UserRepository repo,
                       PasswordEncoder encoder,
                       OTPService otpService,
                       EmailService emailService) {
        this.repo = repo;
        this.encoder = encoder;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    public void register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        repo.save(user);
    }

    public void generateAndSendOTP(User user) {
        String otp = otpService.generateOTP();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        user.setOtpVerified(false);
        repo.save(user);
        emailService.sendOTP(user.getEmail(), otp);
    }
}
