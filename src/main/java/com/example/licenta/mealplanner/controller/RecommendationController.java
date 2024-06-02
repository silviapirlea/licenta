package com.example.licenta.mealplanner.controller;

import com.example.licenta.mealplanner.dto.RecommendationDto;
import com.example.licenta.mealplanner.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping()
    public ResponseEntity<RecommendationDto> getRecommendation() {
        return ResponseEntity.ok(recommendationService.getRecommendation());
    }
}
