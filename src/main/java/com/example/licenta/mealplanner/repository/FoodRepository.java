package com.example.licenta.mealplanner.repository;

import com.example.licenta.mealplanner.model.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    boolean existsByName(String name);

    Optional<Food> findByName(String name);

    Page<Food> findAllByNameContainsIgnoreCase(Pageable pageable, String search);
}
