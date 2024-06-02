package com.example.licenta.mealplanner.dto;

import com.example.licenta.mealplanner.model.FoodDetails;
import com.example.licenta.mealplanner.model.Recipe;
import com.example.licenta.mealplanner.model.enums.MealType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealDto {
    private Long id;
    private MealType mealType;
    private Set<FoodDetails> foods;
    private Set<Recipe> recipes;
    private List<Double> nutrients; // calories, proteins, fats, carbs
}
