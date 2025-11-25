package com.ccdweb.springboot.jpa.meallog;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccdweb.springboot.jpa.UserEntity;
import com.ccdweb.springboot.jpa.UserRepository;
import com.ccdweb.springboot.jpa.userlog.UserLogEntity;
import com.ccdweb.springboot.jpa.userlog.UserLogRepository;
import com.ccdweb.springboot.jpa.userlog.UserLogResponseDTO;

import jakarta.transaction.Transactional;

@Service
public class MealLogService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private UserLogRepository userLogRepository;

    // 실제 혈당 예측 모델 호출 부분 (임시로 120~200 랜덤)
    private int predictGlucose(MealLogEntity meal) {
        return 120 + (int)(Math.random() * 80);
    }

    /**
     * 전체 흐름:
     * 1. 식단 및 영양정보 저장 (meal_log table)
     * 2. 모델로 식후 2시간 혈당 예측
     * 3. 혈당 로그 저장 (user_log table)
     * 4. DTO로 변환하여 각각 식단 추천 / 캘린더 시각화(UserLogService)에 반환
     */

    @Transactional
    public UserLogResponseDTO saveMealAndPredict(String userId, MealLogRequestDTO dto) {

        // 1) 사용자 조회
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));

        // 2) 식단 로그 입력
        MealLogEntity meal = new MealLogEntity();
        meal.setUser(user);
        meal.setMeal_description(dto.getMealDescription());
        meal.setLogTime(LocalDateTime.now());
        mealLogRepository.save(meal);  // mealId 생성됨

        // 3) 모델 혈당 예측 부분 추후 수정 필요
        int predicted = predictGlucose(meal);

        // 4) 혈당 로그 입력 (meal_id FK 연동)
        UserLogEntity log = new UserLogEntity();
        log.setUser(user);
        log.setMeal(meal);               // FK 설정
        log.setGlucose(predicted);
        log.setLogTime(LocalDateTime.now());
        userLogRepository.save(log);

        // 5) DTO로 변환 (프론트에 반환)
        return UserLogResponseDTO.builder()
                .glucose(log.getGlucose())
                .logTime(log.getLogTime())
                .build();
    }    
}
