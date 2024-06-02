package com.example.licenta.mealplanner.service;

import com.example.licenta.auth.security.services.UserDetailsImpl;
import com.example.licenta.mealplanner.dto.MealDto;
import com.example.licenta.mealplanner.dto.NutrientsDto;
import com.example.licenta.mealplanner.mapper.MealMapper;
import com.example.licenta.mealplanner.model.FoodDetails;
import com.example.licenta.mealplanner.model.Meal;
import com.example.licenta.mealplanner.model.Recipe;
import com.example.licenta.mealplanner.repository.FoodDetailsRepository;
import com.example.licenta.mealplanner.repository.MealRepository;
import com.example.licenta.mealplanner.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealService {
    private final MealRepository mealRepository;
    private final RecipeRepository recipeRepository;
    private final FoodDetailsRepository foodDetailsRepository;
    private final FoodService foodService;
    private final MealMapper mealMapper;

    public boolean existsByMealTypeAndDateAndUsername(Meal meal) {
        return mealRepository.existsByMealTypeAndDateAndUsername(meal.getMealType(), meal.getDate(), meal.getUsername());
    }

    public Meal saveMeal(Meal meal) {
        log.info("Adding a new meal in db {}", meal);
        Set<FoodDetails> foodDetailsSet = new HashSet<>();
        Set<Recipe> recipeSet = new HashSet<>();

        addFoodsOrRecipes(meal, foodDetailsSet, recipeSet);

        return mealRepository.save(
                Meal.builder().username(getCurrentUsername())
                .mealType(meal.getMealType())
                .date(LocalDate.now())
                .foods(foodDetailsSet)
                .recipes(recipeSet)
                .build()
        );
    }

    public Meal updateMeal(Long id, Meal newMeal) {
        Set<FoodDetails> foodDetailsSet = new HashSet<>();
        Set<Recipe> recipeSet = new HashSet<>();
        var oldMeal = mealRepository.findById(id).orElseThrow(() -> new RuntimeException("Meal not found"));

        if(!Objects.equals(getCurrentUsername(), oldMeal.getUsername())) {
            throw new RuntimeException("Cannot update this meal because it do not belong to the current user");
        }

        addFoodsOrRecipes(newMeal, foodDetailsSet, recipeSet);

        oldMeal.setRecipes(recipeSet);
        oldMeal.setFoods(foodDetailsSet);

        log.info("Update meal in db {}", newMeal);
        return mealRepository.save(oldMeal);
    }

    public List<MealDto> findMealsForDay(LocalDate date) {
        return mealRepository.findAllByDateAndUsername(date, getCurrentUsername()).stream()
                .map(meal -> mealMapper.toDto(meal, getMealNutrients(meal)))
                .collect(Collectors.toList());

    }

    public MealDto findById(Long id) {
        return mealRepository.findById(id)
                .map(meal -> mealMapper.toDto(meal, getMealNutrients(meal)))
                .orElseThrow(() -> new RuntimeException("Meal not found"));
    }

    public List<NutrientsDto> getNutrientsForMultipleDays(int noOfDays) {
        if(noOfDays < 0) {
            log.error("Number of days should be positive!");
            throw new RuntimeException("Number of days should be positive!");
        }
        List<NutrientsDto> list = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusDays(noOfDays);
        int day = 0;
        while(day < noOfDays) {
            var dateToSearch = startDate.plusDays(day);
            var meals = findMealsForDay(dateToSearch);
            var nutrients = calculateNutrientsForADay(meals);
            var dto = NutrientsDto.builder()
                    .date(dateToSearch)
                    .day(dateToSearch.getDayOfWeek().name())
                    .calories(nutrients.get(0))
                    .proteins(nutrients.get(1))
                    .fats(nutrients.get(2))
                    .carbohydrates(nutrients.get(3))
                    .build();
            list.add(dto);
            day++;
        }
        return list;
    }

    private String getCurrentUsername() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    private List<Double> calculateNutrientsForADay(List<MealDto> mealDtos) {
        AtomicReference<Double> calories = new AtomicReference<>(0d);
        AtomicReference<Double> proteins = new AtomicReference<>(0d);
        AtomicReference<Double> fats = new AtomicReference<>(0d);
        AtomicReference<Double> carbs = new AtomicReference<>(0d);
        if(mealDtos.size() > 0) {
            mealDtos.forEach(mealDto -> {
                var nutrients = mealDto.getNutrients();
                calories.updateAndGet(v -> v + nutrients.get(0));
                proteins.updateAndGet(v -> v + nutrients.get(1));
                fats.updateAndGet(v -> v + nutrients.get(2));
                carbs.updateAndGet(v -> v + nutrients.get(3));
            });
        }
        return List.of(calories.get(), proteins.get(), fats.get(), carbs.get());
    }

    private List<Double> getMealNutrients(Meal meal) {
        AtomicReference<Double> calories = new AtomicReference<>(0d);
        AtomicReference<Double> proteins = new AtomicReference<>(0d);
        AtomicReference<Double> fats = new AtomicReference<>(0d);
        AtomicReference<Double> carbs = new AtomicReference<>(0d);

        meal.getFoods().forEach(foodDetails -> {
            var food = foodService.findFoodByName(foodDetails.getName());
            calories.updateAndGet(v -> v + (foodDetails.getCount() * food.getCalories()) / food.getGrams());
            proteins.updateAndGet(v -> v + (foodDetails.getCount() * food.getProteins()) / food.getGrams());
            fats.updateAndGet(v -> v + (foodDetails.getCount() * food.getFats()) / food.getGrams());
            carbs.updateAndGet(v -> v + (foodDetails.getCount() * food.getCarbohydrates()) / food.getGrams());
        });

        meal.getRecipes().stream()
                .map(Recipe::getFoods)
                .forEach(foodDetailsSet -> {
                    foodDetailsSet.forEach(foodDetails -> {
                        var food = foodService.findFoodByName(foodDetails.getName());
                        calories.updateAndGet(v -> v + (foodDetails.getCount() * food.getCalories()) / food.getGrams());
                        proteins.updateAndGet(v -> v + (foodDetails.getCount() * food.getProteins()) / food.getGrams());
                        fats.updateAndGet(v -> v + (foodDetails.getCount() * food.getFats()) / food.getGrams());
                        carbs.updateAndGet(v -> v + (foodDetails.getCount() * food.getCarbohydrates()) / food.getGrams());
                    });
                });

        return List.of(calories.get(), proteins.get(), fats.get(), carbs.get());
    }

    private void addFoodsOrRecipes(Meal meal, Set<FoodDetails> foodDetailsSet, Set<Recipe> recipeSet) {
        meal.getFoods().forEach(foodDetails -> {
            if(!foodDetailsRepository.existsByNameAndAndCount(foodDetails.getName(), foodDetails.getCount())) {
                foodDetailsRepository.save(foodDetails);
            }
            FoodDetails foodDetails1 = foodDetailsRepository.
                    findByNameAndCount(foodDetails.getName(), foodDetails.getCount())
                    .orElseThrow(() -> new RuntimeException("Food not found"));
            foodDetailsSet.add(foodDetails1);
        });
        meal.getRecipes().forEach(recipe -> {
            Recipe recipe1 = recipeRepository.findByName(recipe.getName()).orElseThrow(() -> new RuntimeException("Recipe not found"));
            recipeSet.add(recipe1);
        });
    }
}
