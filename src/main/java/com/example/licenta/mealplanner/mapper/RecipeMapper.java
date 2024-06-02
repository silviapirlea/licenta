package com.example.licenta.mealplanner.mapper;

import com.example.licenta.mealplanner.dto.RecipeDto;
import com.example.licenta.mealplanner.model.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(target = "id", source = "recipe.id")
    @Mapping(target = "name", source = "recipe.name")
    @Mapping(target = "foods", source = "recipe.foods")
    @Mapping(target = "totalCalories", source = "totalCalories")
    @Mapping(target = "totalProteins", source = "totalProteins")
    @Mapping(target = "totalFats", source = "totalFats")
    @Mapping(target = "totalCarbohydrates", source = "totalCarbohydrates")
    RecipeDto toDto(Recipe recipe, Double totalCalories, Double totalProteins, Double totalFats, Double totalCarbohydrates);
}
