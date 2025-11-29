package com.ccdweb.springboot.nutrition.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionResponse {
    private List<FoodNutritionDetail> foods;
    private Double totalCarbohydrates;
    private Double totalProtein;
    private Double totalSugars;
    private Double totalFat;
    private Double totalEnergy;
}
