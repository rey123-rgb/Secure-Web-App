package com.secureapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.secureapp.model.User;
import com.secureapp.repository.UserRepository;
import com.secureapp.service.UserService;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserService service;
    private final UserRepository repo;

    public AuthController(UserService service, UserRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        service.register(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/otp-page")
    public String otpPage(Principal principal) {
        User user = repo.findByUsername(principal.getName()).get();
        service.generateAndSendOTP(user);
        return "otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp, Principal principal) {
        User user = repo.findByUsername(principal.getName()).get();

        if (user.getOtp().equals(otp) &&
            user.getOtpExpiry().isAfter(java.time.LocalDateTime.now())) {

            user.setOtpVerified(true);
            repo.save(user);

            if (user.getRole() == com.secureapp.model.Role.ADMIN)
                return "redirect:/admin/dashboard";
            else
                return "redirect:/user/home";
        }

        return "redirect:/otp-page?error";
    }
}
