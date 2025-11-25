package com.ccdweb.springboot.jpa.meallog;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccdweb.springboot.jpa.UserEntity;

@Repository
public interface MealLogRepository extends JpaRepository<MealLogEntity, UUID>{
    // 사용자의 가장 최근 식단 1개 
	Optional<MealLogEntity> findTopByUserOrderByLogTimeDesc(UserEntity user);
}
