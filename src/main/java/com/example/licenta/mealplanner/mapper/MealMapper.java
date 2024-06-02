package com.example.licenta.mealplanner.mapper;

import com.example.licenta.mealplanner.dto.MealDto;
import com.example.licenta.mealplanner.model.Meal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MealMapper {

    @Mapping(target = "id", source = "meal.id")
    @Mapping(target = "mealType", source = "meal.mealType")
    @Mapping(target = "foods", source = "meal.foods")
    @Mapping(target = "recipes", source = "meal.recipes")
    @Mapping(target = "nutrients", source = "nutrients")
    MealDto toDto(Meal meal, List<Double> nutrients);

    @Mapping(target = "id", ignore = true)
    void updateMeal(@MappingTarget Meal mealToUpdate, Meal meal);

}
