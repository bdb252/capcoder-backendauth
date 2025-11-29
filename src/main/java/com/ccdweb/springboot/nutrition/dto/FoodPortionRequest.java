package com.ccdweb.springboot.nutrition.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodPortionRequest {
    private String foodName;
    private Double portion;
}
