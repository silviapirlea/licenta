package com.example.licenta.mealplanner.dto;

import com.example.licenta.mealplanner.model.FoodDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {
    private Long id;
    private String name;
    private Set<FoodDetails> foods;
    private Double totalCalories;
    private Double totalProteins;
    private Double totalFats;
    private Double totalCarbohydrates;
}
