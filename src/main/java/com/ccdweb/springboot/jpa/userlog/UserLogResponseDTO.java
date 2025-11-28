package com.ccdweb.springboot.jpa.userlog;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogResponseDTO {
	private UUID logId;
	private Integer glucose;
	private LocalDateTime logTime;
	
	public static UserLogResponseDTO fromEntity(UserLogEntity entity) {
		System.out.println("DTO logId: "+entity.getLogid());
		return UserLogResponseDTO.builder()
				.logId(entity.getLogid())
				.glucose(entity.getGlucose())
				.logTime(entity.getLogTime())
				.build();
	}
}
