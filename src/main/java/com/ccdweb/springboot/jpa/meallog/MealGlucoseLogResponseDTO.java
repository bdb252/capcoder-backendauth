package com.ccdweb.springboot.jpa.meallog;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ccdweb.springboot.jpa.userlog.UserLogEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MealGlucoseLogResponseDTO {
	private UUID logId;            // user_log PK
    private UUID mealId;           // meal_log PK
    private String mealDescription;
    private Integer glucose;
    private LocalDateTime logTime; // 혈당 기록 시간 기준
	
	public static MealGlucoseLogResponseDTO fromEntity(UserLogEntity entity) {
		return MealGlucoseLogResponseDTO.builder()
				.logTime(entity.getLogTime())
				.build();
	}
}
