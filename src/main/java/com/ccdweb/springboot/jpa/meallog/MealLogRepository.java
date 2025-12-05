package com.ccdweb.springboot.jpa.meallog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ccdweb.springboot.jpa.UserEntity;

@Repository
public interface MealLogRepository extends JpaRepository<MealLogEntity, UUID>{
    // 사용자의 가장 최근 식단 1개 
	Optional<MealLogEntity> findTopByUserOrderByLogTimeDesc(UserEntity user);
    
    // top-k 음식 
    @Query("SELECT m.meal_description " +
           "FROM MealLogEntity m " +
           "WHERE m.user.userId = :userId " +
           "AND m.logTime >= :start " +
           "AND m.logTime < :end")
    List<String> findMealDescriptionsByUserAndDateRange(
            @Param("userId") String userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
