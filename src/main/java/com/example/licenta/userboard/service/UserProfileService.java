package com.example.licenta.userboard.service;

import com.example.licenta.auth.security.services.UserDetailsImpl;
import com.example.licenta.userboard.mappers.UserProfileMapper;
import com.example.licenta.userboard.model.UserProfile;
import com.example.licenta.userboard.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfile updateUserProfile(UserProfile userProfileToUpdate) {
        if(userProfileRepository.existsByUsername(getCurrentUsername())) {
            var oldProfile = userProfileRepository.findByUsername(getCurrentUsername());
            return updateExistingProfile(userProfileToUpdate, oldProfile.get());
        }
        else {
            return createProfile(userProfileToUpdate);
        }
    }

    public void updateMealPlanInProfile(String username, boolean hasMealPlan) {
        var profile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setHasMealPlan(hasMealPlan);
        userProfileRepository.save(profile);
    }

    public UserProfile getUserProfile(String username) {
        var profile = userProfileRepository.findByUsername(username);
        return profile.orElseGet(() -> UserProfile.builder().build());
    }

    public String getCurrentUsername() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    private UserProfile updateExistingProfile(UserProfile profileToUpdate, UserProfile oldProfile) {
        userProfileMapper.updateUserProfile(oldProfile, profileToUpdate);
        return userProfileRepository.save(oldProfile);
    }

    private UserProfile createProfile(UserProfile userProfile) {
        userProfile.setUsername(getCurrentUsername());
        return userProfileRepository.save(userProfile);
    }
}
