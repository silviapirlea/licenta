package com.example.licenta.mealplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    Double REE;
    Double TDEE;
    Double fats;
    Double proteins;
    Double carbohydrates;
}
