package com.example.licenta.mealplanner.controller;

import com.example.licenta.auth.payload.response.MessageResponse;
import com.example.licenta.mealplanner.model.Recipe;
import com.example.licenta.mealplanner.model.RecipePage;
import com.example.licenta.mealplanner.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipe")
public class RecipeController {
    public static final String RECIPE_ALREADY_EXISTS = "Recipe already exists";
    public static final String RECIPE_DELETED = "Recipe deleted successfully";

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "10";
    private static final String DEFAULT_SEARCH_TERM = "";

    private final RecipeService recipeService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRecipe(@Valid @RequestBody Recipe recipe) {
        if(recipeService.existsByName(recipe.getName())) {
            return ResponseEntity.badRequest().body(new MessageResponse(RECIPE_ALREADY_EXISTS));
        }
        return ResponseEntity.ok(recipeService.addRecipe(recipe));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok(new MessageResponse(RECIPE_DELETED));
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<RecipePage> getRecipes(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) int size,
            @RequestParam(value = "searchTerm", defaultValue = DEFAULT_SEARCH_TERM, required = false) String searchTerm
    ) {
        return ResponseEntity.ok(recipeService.getRecipes(page, size, searchTerm));
    }
}
