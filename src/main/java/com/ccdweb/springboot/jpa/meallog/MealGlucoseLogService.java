package com.ccdweb.springboot.jpa.meallog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccdweb.springboot.jpa.UserEntity;
import com.ccdweb.springboot.jpa.UserRepository;
import com.ccdweb.springboot.jpa.userlog.UserLogEntity;
import com.ccdweb.springboot.jpa.userlog.UserLogRepository;

import jakarta.transaction.Transactional;

@Service
public class MealGlucoseLogService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private UserLogRepository userLogRepository;

    /**
     * 전체 흐름:
     * 1. 식단 및 영양정보 저장 (meal_log table)
     * 2. 모델로 식후 2시간 혈당 예측
     * 3. 혈당 로그 저장 (user_log table)
     * 4. DTO로 변환하여 각각 식단 추천 / 캘린더 시각화(UserLogService)에 반환
     */

    @Transactional
    public MealGlucoseLogResponseDTO saveMealAndGlucose(String userId, 
    		MealGlucoseLogRequestDTO dto) {

        // 1) 사용자 조회
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));

        // 2) 식단 로그 입력
        MealLogEntity meal = new MealLogEntity();
        meal.setUser(user);
        meal.setMeal_description(dto.getMealDescription());
        mealLogRepository.save(meal);  // mealId 생성됨

        // 3) 혈당 로그 입력 (meal_id FK 연동)
        UserLogEntity log = new UserLogEntity();
        log.setUser(user);
        log.setMeal(meal);               // FK 설정
        log.setGlucose(dto.getGlucose());
        log.setLogTime(LocalDateTime.now());
        userLogRepository.save(log);

        // 4) 응답 DTO 구성
        return MealGlucoseLogResponseDTO.builder()
        		.logId(log.getLogid())
                .mealId(meal.getMealid())
                .mealDescription(meal.getMeal_description())
                .glucose(log.getGlucose())
                .logTime(log.getLogTime())
                .build();
    }   
    
    // 기록 삭제
    @Transactional
    public void deleteLogs(String userId, UUID logId){
        UserLogEntity log = userLogRepository.findByLogidAndUserUserId(logId, userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 기록이 없습니다."));
        
        // 연결된 식단
        MealLogEntity meal = log.getMeal(); 
        
        // 1) 먼저 user_log 삭제 (FK 때문에)
        userLogRepository.delete(log);

        // 2) 그 다음 연결된 meal_log 삭제
        if (meal != null) {
            mealLogRepository.delete(meal);
        }
    }

    public List<TopKMealDTO> getTopKMealofMonth(String userId, int year, int month, int limit){
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDateTime start = firstDay.atStartOfDay();
        LocalDateTime end   = firstDay.plusMonths(1).atStartOfDay();

        // 1) 해당 달의 모든 meal_description 가져오기
        List<String> descriptions = mealLogRepository
                .findMealDescriptionsByUserAndDateRange(userId, start, end);

        // 2) "," 기준으로 split해서 음식 이름 뽑고, trim 후 카운트
        Map<String, Long> countMap = descriptions.stream()
                .filter(Objects::nonNull)
                .flatMap(desc -> Arrays.stream(desc.split(","))) // "떡볶이", " 삶은 계란"
                .map(String::trim)                              // "떡볶이", "삶은 계란"
                .filter(s -> !s.isEmpty())
                .collect(Collectors.groupingBy(
                        s -> s,
                        Collectors.counting()
                ));
                
        // 3) 많이 먹은 순으로 정렬해서 상위 limit개 자르기
        return countMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(e -> new TopKMealDTO(e.getKey(), e.getValue()))
                .toList(); 
    }

}
