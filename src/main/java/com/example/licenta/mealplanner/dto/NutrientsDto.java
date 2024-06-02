package com.example.licenta.mealplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutrientsDto {
    private LocalDate date;
    private String day;
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
}
