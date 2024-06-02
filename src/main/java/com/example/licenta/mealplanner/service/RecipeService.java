package com.example.licenta.mealplanner.service;

import com.example.licenta.mealplanner.dto.RecipeDto;
import com.example.licenta.mealplanner.mapper.RecipeMapper;
import com.example.licenta.mealplanner.model.Food;
import com.example.licenta.mealplanner.model.FoodDetails;
import com.example.licenta.mealplanner.model.Recipe;
import com.example.licenta.mealplanner.model.RecipePage;
import com.example.licenta.mealplanner.repository.FoodDetailsRepository;
import com.example.licenta.mealplanner.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final FoodDetailsRepository foodDetailsRepository;

    private final RecipeMapper recipeMapper;

    private final FoodService foodService;

    public Recipe addRecipe(Recipe recipe) {
        log.info("Adding a new recipe in db {}", recipe);
        Set<FoodDetails> foodDetailsSet = new HashSet<>();
        recipe.getFoods().forEach(foodDetails -> {
            if(!foodDetailsRepository.existsByNameAndAndCount(foodDetails.getName(), foodDetails.getCount())) {
                foodDetailsRepository.save(foodDetails);
            }
            FoodDetails foodDetails1 = foodDetailsRepository.
                    findByNameAndCount(foodDetails.getName(), foodDetails.getCount())
                    .orElseThrow(() -> new RuntimeException("Food not found"));
            foodDetailsSet.add(foodDetails1);
        });
        return recipeRepository.save(Recipe.builder().name(recipe.getName()).foods(foodDetailsSet).build());
    }

    public boolean existsByName(String name) {
        return recipeRepository.existsByName(name);
    }

    public void deleteRecipe(Long id) {
        log.info("Deleting recipe with id {}", id);
        recipeRepository.deleteById(id);
    }

    public RecipePage getRecipes(int page, int size, String searchTerm) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeRepository.findAllByNameContainsIgnoreCase(pageable, searchTerm);
        List<RecipeDto> dtoItems = recipePage.getContent().stream()
                .map(recipe -> recipeMapper.toDto(recipe, calculateTotalCalories(recipe.getFoods()),
                        calculateTotalProteins(recipe.getFoods()), calculateTotalFats(recipe.getFoods()),
                        calculateTotalCarbohydrates(recipe.getFoods())))
                .collect(Collectors.toList());
        return RecipePage.builder()
                .page(recipePage.getNumber())
                .size(recipePage.getSize())
                .totalElements(recipePage.getTotalElements())
                .totalPages(recipePage.getTotalPages())
                .last(recipePage.isLast())
                .items(dtoItems)
                .build();
    }

    private Double calculateTotalCalories(Set<FoodDetails> foodDetailsSet) {
        AtomicReference<Double> sum = new AtomicReference<>(0d);
        foodDetailsSet.forEach(foodDetails -> {
            var food = foodService.findFoodByName(foodDetails.getName());
            sum.updateAndGet(v -> v + (foodDetails.getCount() * food.getCalories()) / food.getGrams());
        });
        return sum.get();
    }

    private Double calculateTotalProteins(Set<FoodDetails> foodDetailsSet) {
        AtomicReference<Double> sum = new AtomicReference<>(0d);
        foodDetailsSet.forEach(foodDetails -> {
            var food = foodService.findFoodByName(foodDetails.getName());
            sum.updateAndGet(v -> v + (foodDetails.getCount() * food.getProteins()) / food.getGrams());
        });
        return sum.get();
    }

    private Double calculateTotalFats(Set<FoodDetails> foodDetailsSet) {
        AtomicReference<Double> sum = new AtomicReference<>(0d);
        foodDetailsSet.forEach(foodDetails -> {
            var food = foodService.findFoodByName(foodDetails.getName());
            sum.updateAndGet(v -> v + (foodDetails.getCount() * food.getFats()) / food.getGrams());
        });
        return sum.get();
    }

    private Double calculateTotalCarbohydrates(Set<FoodDetails> foodDetailsSet) {
        AtomicReference<Double> sum = new AtomicReference<>(0d);
        foodDetailsSet.forEach(foodDetails -> {
            var food = foodService.findFoodByName(foodDetails.getName());
            sum.updateAndGet(v -> v + (foodDetails.getCount() * food.getCarbohydrates()) / food.getGrams());
        });
        return sum.get();
    }
}
