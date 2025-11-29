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
public class NutritionCalculateRequest {
    private List<FoodPortionRequest> foods;
}
