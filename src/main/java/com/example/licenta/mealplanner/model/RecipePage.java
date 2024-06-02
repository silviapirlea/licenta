package com.example.licenta.mealplanner.model;

import com.example.licenta.mealplanner.dto.RecipeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipePage {
    List<RecipeDto> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
