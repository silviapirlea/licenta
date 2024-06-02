package com.example.licenta.mealplanner.controller;

import com.example.licenta.auth.payload.response.MessageResponse;
import com.example.licenta.mealplanner.model.Food;
import com.example.licenta.mealplanner.model.FoodPage;
import com.example.licenta.mealplanner.service.FoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/food")
public class FoodController {

    private static final String FOOD_ALREADY_EXISTS = "Food already exists";
    private static final String FOOD_DELETED = "Food deleted";

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "10";
    private static final String DEFAULT_SEARCH_TERM = "";
    private final FoodService foodService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFood(@Valid @RequestBody Food food) {
        if(foodService.existsByName(food.getName()))
            return ResponseEntity.badRequest().body(new MessageResponse(FOOD_ALREADY_EXISTS));
        return ResponseEntity.ok(foodService.saveFood(food));
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FoodPage> getFoods(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) int size,
            @RequestParam(value = "searchTerm", defaultValue = DEFAULT_SEARCH_TERM, required = false) String searchTerm
    ) {
        return ResponseEntity.ok(foodService.getFoods(page, size, searchTerm));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.ok(new MessageResponse(FOOD_DELETED));
    }
}
