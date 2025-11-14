package com.ccdweb.springboot.nutrition.repository;

import com.ccdweb.springboot.nutrition.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FoodRepository extends JpaRepository<FoodEntity, UUID> {

    Optional<FoodEntity> findByFoodCode(String foodCode);

    List<FoodEntity> findByFoodNameContaining(String keyword);

    List<FoodEntity> findByCategoryLarge(String category);

    boolean existsByFoodCode(String foodCode);
}
