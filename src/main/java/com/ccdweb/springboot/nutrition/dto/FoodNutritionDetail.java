package com.ccdweb.springboot.nutrition.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodNutritionDetail {
    private String foodName;
    private Double portion;
    private Double carbohydrates;
    private Double protein;
    private Double sugars;
    private Double fat;
    private Double energy;
}
