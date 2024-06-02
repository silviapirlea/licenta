package com.example.licenta.auth.controller;

import com.example.licenta.email.model.Email;
import com.example.licenta.email.service.EmailNotificationService;
import com.example.licenta.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    private final EmailNotificationService emailNotificationService;
    private final EmailService emailService;

    @GetMapping("/all")
    public String allAccess() {
        return "Public content.";
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmail(@RequestBody Email email)    {
//        this.emailNotificationService.sendWelcomeEmail();
        this.emailService.sendSimpleMessage(email);
        return ResponseEntity.ok("Email sent");
    }


    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess() {
        this.emailNotificationService.sendWelcomeEmail();
        return "User content.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin board.";
    }
}
