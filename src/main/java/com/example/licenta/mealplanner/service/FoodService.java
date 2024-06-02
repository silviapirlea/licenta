package com.example.licenta.mealplanner.service;

import com.example.licenta.mealplanner.mapper.FoodMapper;
import com.example.licenta.mealplanner.model.Food;
import com.example.licenta.mealplanner.model.FoodPage;
import com.example.licenta.mealplanner.repository.FoodDetailsRepository;
import com.example.licenta.mealplanner.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodService {
    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;

    public Food saveFood(Food food) {
        log.info("Adding a new food in the system {}", food);

        return foodRepository.save(food);
    }

    public boolean existsByName(String name) {
        return foodRepository.existsByName(name);
    }

    public void deleteFood(Long id) {
        log.info("Deleting food with id {}", id);
        foodRepository.deleteById(id);
    }

    public FoodPage getFoods(int page, int size, String searchTerm) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Food> foodPage = foodRepository.findAllByNameContainsIgnoreCase(pageable, searchTerm);
        return foodMapper.toFoodPage(foodPage);
    }

    public Food findFoodByName(String name) {
        return foodRepository.findByName(name).orElseThrow();
    }

}
