package com.example.licenta.mealplanner.mapper;

import com.example.licenta.mealplanner.dto.FoodDto;
import com.example.licenta.mealplanner.model.Food;
import com.example.licenta.mealplanner.model.FoodPage;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodMapper {

    FoodDto toDto(Food food);

    Food toEntity(FoodDto dto);

    List<FoodDto> toFoodDtoList(Page<Food> foodPage);

    default FoodPage toFoodPage(Page<Food> foodPage) {
        return FoodPage.builder()
                .page(foodPage.getNumber())
                .size(foodPage.getSize())
                .totalElements(foodPage.getTotalElements())
                .totalPages(foodPage.getTotalPages())
                .last(foodPage.isLast())
                .items(toFoodDtoList(foodPage))
                .build();
    }
}
