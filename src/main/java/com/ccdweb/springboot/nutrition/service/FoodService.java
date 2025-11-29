package com.ccdweb.springboot.nutrition.service;

import com.ccdweb.springboot.nutrition.dto.FoodNutritionDetail;
import com.ccdweb.springboot.nutrition.dto.FoodPortionRequest;
import com.ccdweb.springboot.nutrition.dto.NutritionResponse;
import com.ccdweb.springboot.nutrition.entity.FoodEntity;
import com.ccdweb.springboot.nutrition.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public NutritionResponse calculateTotalNutrition(List<FoodPortionRequest> foods) {
        List<FoodNutritionDetail> foodDetails = new ArrayList<>();
        double totalCarbohydrates = 0;
        double totalProtein = 0;
        double totalSugars = 0;
        double totalFat = 0;
        double totalEnergy = 0;

        for (FoodPortionRequest foodRequest : foods) {
            List<FoodEntity> searchResults = foodRepository.findByFoodNameContaining(foodRequest.getFoodName());

            FoodEntity food = searchResults.stream()
                    .filter(f -> f.getFoodName().equals(foodRequest.getFoodName()))
                    .findFirst()
                    .orElse(null);

            if (food != null) {
                double portion = foodRequest.getPortion() != null ? foodRequest.getPortion() : 1.0;

                double carbs = (food.getCarbohydrates() != null ? food.getCarbohydrates() : 0) * portion;
                double protein = (food.getProtein() != null ? food.getProtein() : 0) * portion;
                double sugars = (food.getSugars() != null ? food.getSugars() : 0) * portion;
                double fat = (food.getFat() != null ? food.getFat() : 0) * portion;
                double energy = (food.getEnergy() != null ? food.getEnergy() : 0) * portion;

                foodDetails.add(new FoodNutritionDetail(foodRequest.getFoodName(), portion, carbs, protein, sugars, fat, energy));

                totalCarbohydrates += carbs;
                totalProtein += protein;
                totalSugars += sugars;
                totalFat += fat;
                totalEnergy += energy;
            }
        }

        return new NutritionResponse(foodDetails, totalCarbohydrates, totalProtein, totalSugars, totalFat, totalEnergy);
    }
}
