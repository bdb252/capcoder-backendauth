package com.ccdweb.springboot.jpa.userlog;


import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLogRequestDTO {
	private Integer glucose;
	private UUID mealId;
}
