package com.ccdweb.springboot.nutrition.service;

import com.ccdweb.springboot.nutrition.entity.FoodEntity;
import com.ccdweb.springboot.nutrition.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    public List<FoodEntity> searchByName(String keyword) {
        return foodRepository.findByFoodNameContaining(keyword);
    }

    public Optional<FoodEntity> getByCode(String code) {
        return foodRepository.findByFoodCode(code);
    }

    public List<FoodEntity> getByCategory(String category) {
        return foodRepository.findByCategoryLarge(category);
    }
}
