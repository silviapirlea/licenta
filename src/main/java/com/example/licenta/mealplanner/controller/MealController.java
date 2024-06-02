package com.example.licenta.mealplanner.controller;

import com.example.licenta.auth.payload.response.MessageResponse;
import com.example.licenta.mealplanner.dto.NutrientsDto;
import com.example.licenta.mealplanner.model.Meal;
import com.example.licenta.mealplanner.service.MealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meal")
public class MealController {
    private static final String MEAL_ALREADY_EXISTS = "Meal already exists";
    private static  LocalDate DEFAULT_DATE = LocalDate.now();
    private static final String defaultNoOfDays = "7";
    private final MealService mealService;

    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createMeal(@Valid @RequestBody Meal meal) {
        return ResponseEntity.ok(mealService.saveMeal(meal));
    }

    @GetMapping("/day")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMealsForDay(@RequestParam(value = "date", required = false) LocalDate date) {
        LocalDate dateToSearch = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(mealService.findMealsForDay(dateToSearch));
    }

    @GetMapping("/week")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<NutrientsDto>> getMealsForMultipleDays(
            @RequestParam(value = "days", defaultValue = defaultNoOfDays, required = false) int days) {
        return ResponseEntity.ok(mealService.getNutrientsForMultipleDays(days));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMealById(@PathVariable Long id) {
        return ResponseEntity.ok(mealService.findById(id));
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateMeal(@PathVariable Long id, @Valid @RequestBody Meal meal){
        return ResponseEntity.ok(mealService.updateMeal(id, meal));
    }
}
