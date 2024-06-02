package com.example.licenta.userboard.controller;

import com.example.licenta.userboard.model.UserProfile;
import com.example.licenta.userboard.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-profile")
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PutMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserProfile> updateProfile(@Valid @RequestBody UserProfile userProfile) {
        return ResponseEntity.ok(userProfileService.updateUserProfile(userProfile));
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserProfile> getProfile(@PathVariable String username) {
        return ResponseEntity.ok(userProfileService.getUserProfile(username));
    }
}
