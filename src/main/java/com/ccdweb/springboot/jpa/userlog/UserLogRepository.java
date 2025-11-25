package com.ccdweb.springboot.jpa.userlog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccdweb.springboot.jpa.meallog.MealLogEntity;

@Repository
public interface UserLogRepository extends JpaRepository<UserLogEntity, UUID> {
	List<UserLogEntity> findByUser_UserIdAndLogTimeBetween(String userId, LocalDateTime start, LocalDateTime end);
	
	// 특정 식단(meal)에 대해 가장 최근 혈당 로그
	Optional<UserLogEntity> findByMeal(MealLogEntity meal);
}
