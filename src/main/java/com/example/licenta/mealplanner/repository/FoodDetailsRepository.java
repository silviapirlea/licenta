package com.example.licenta.mealplanner.repository;

import com.example.licenta.mealplanner.model.FoodDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodDetailsRepository extends JpaRepository<FoodDetails, Long> {
    Optional<FoodDetails> findByNameAndCount(String name, Double count);

    boolean existsByNameAndAndCount(String name, Double count);
}
