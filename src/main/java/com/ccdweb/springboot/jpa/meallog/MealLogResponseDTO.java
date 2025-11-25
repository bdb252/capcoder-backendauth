package com.ccdweb.springboot.jpa.meallog;

import java.time.LocalDateTime;

import com.ccdweb.springboot.jpa.userlog.UserLogEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MealLogResponseDTO {
	private LocalDateTime logTime;
	
	public static MealLogResponseDTO fromEntity(UserLogEntity entity) {
		return MealLogResponseDTO.builder()
				.logTime(entity.getLogTime())
				.build();
	}
}
