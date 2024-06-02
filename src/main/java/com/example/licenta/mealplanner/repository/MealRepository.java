package com.example.licenta.mealplanner.repository;

import com.example.licenta.mealplanner.model.Meal;
import com.example.licenta.mealplanner.model.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    boolean existsByMealTypeAndDateAndUsername(MealType mealType, LocalDate date, String username);

    List<Meal> findAllByDateAndUsername(LocalDate localDate, String username);
}
