package com.ccdweb.springboot.jpa.userlog;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogResponseDTO {
	private Integer glucose;
	private LocalDateTime logTime;
	
	public static UserLogResponseDTO fromEntity(UserLogEntity entity) {
		return UserLogResponseDTO.builder()
				.glucose(entity.getGlucose())
				.logTime(entity.getLogTime())
				.build();
	}
}
