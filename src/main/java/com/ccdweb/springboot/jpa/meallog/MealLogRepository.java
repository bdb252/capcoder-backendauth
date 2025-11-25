package com.ccdweb.springboot.jpa.meallog;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealLogRepository extends JpaRepository<MealLogEntity, UUID>{
    // List<MealLogEntity> findByUserId();
}
