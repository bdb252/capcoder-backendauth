package com.ccdweb.springboot.nutrition.controller;

import com.ccdweb.springboot.nutrition.entity.FoodEntity;
import com.ccdweb.springboot.nutrition.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping("/search")
    public ResponseEntity<List<FoodEntity>> searchFood(@RequestParam String keyword) {
        List<FoodEntity> results = foodService.searchByName(keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{code}")
    public ResponseEntity<FoodEntity> getFoodByCode(@PathVariable String code) {
        return foodService.getByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<FoodEntity>> getFoodsByCategory(@PathVariable String category) {
        List<FoodEntity> results = foodService.getByCategory(category);
        return ResponseEntity.ok(results);
    }
}
