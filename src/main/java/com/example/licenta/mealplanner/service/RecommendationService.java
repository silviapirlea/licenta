package com.example.licenta.mealplanner.service;

import com.example.licenta.auth.security.services.UserDetailsImpl;
import com.example.licenta.mealplanner.dto.RecommendationDto;
import com.example.licenta.userboard.model.UserProfile;
import com.example.licenta.userboard.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {
    private final UserProfileService userProfileService;

    public RecommendationDto getRecommendation() {
        var profile = userProfileService.getUserProfile(getCurrentUsername());
        if (canCalculateTDEE(profile) && canCalculateREE(profile)) {
            var REE = calculateREE(profile);
            var TDEE = calculateTDEE(profile);
            var weight = Double.parseDouble(profile.getWeight());
            var fats = getFatsRecommendation(TDEE);
            var proteins = getProteinsRecommendation(weight);
            var carbs = getCarbohydratesRecommendation(TDEE, fats, proteins);
            return RecommendationDto.builder()
                    .REE(REE)
                    .TDEE(TDEE)
                    .fats(fats)
                    .proteins(proteins)
                    .carbohydrates(carbs)
                    .build();
        } else return RecommendationDto.builder().build();
    }

    public Double calculateREE(UserProfile profile) {
        if (canCalculateREE(profile)) {
            var weight = Double.parseDouble(profile.getWeight());
            var height = Double.parseDouble(profile.getHeight());
            var age = Double.parseDouble(profile.getAge());
            return switch (profile.getSex()) {
                case "Male" -> 10 * weight + 6.25 * height - 5 * age + 5;
                case "Female" -> 10 * weight + 6.25 * height - 5 * age - 161;
                default -> 0d;
            };
        } else {
            log.error("User profile is not complete for recommendations");
            return 0d;
        }
    }

    public Double calculateTDEE(UserProfile profile) {
        if (canCalculateTDEE(profile) && canCalculateREE(profile)) {
            var REE = calculateREE(profile);
            return switch (profile.getActiveness()) {
                case "Sedentary" -> 1.2 * REE;
                case "Low activeness" -> 1.375 * REE;
                case "Medium activeness" -> 1.55 * REE;
                case "Intense activeness" -> 1.725 * REE;
                default -> 0d;
            };
        } else {
            log.error("User profile is not complete for recommendations");
            return 0d;
        }
    }

    public Double getFatsRecommendation(Double TDEE) {
        return (0.3 * TDEE) / 9;
    }

    public Double getProteinsRecommendation(Double weight) {
        return 1.43 * weight;
    }

    public Double getCarbohydratesRecommendation(Double TDEE, Double fats, Double proteins) {
        return (TDEE - 9 * fats - 4 * proteins) / 4;
    }

    private boolean canCalculateREE(UserProfile userProfile) {
        return userProfile.getWeight() != null
                && userProfile.getHeight() != null
                && userProfile.getAge() != null
                && userProfile.getSex() != null;
    }

    private boolean canCalculateTDEE(UserProfile userProfile) {
        return userProfile.getActiveness() != null;
    }

    private String getCurrentUsername() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}
