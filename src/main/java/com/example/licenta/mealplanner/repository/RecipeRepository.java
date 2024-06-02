package com.example.licenta.mealplanner.repository;

import com.example.licenta.mealplanner.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    boolean existsByName(String name);

    Optional<Recipe> findByName(String name);

    Page<Recipe> findAllByNameContainsIgnoreCase(Pageable pageable, String search);
}
