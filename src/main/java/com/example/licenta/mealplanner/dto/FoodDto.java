package com.example.licenta.mealplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDto {
    private Long id;
    private String name;
    private Integer grams;
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
}
