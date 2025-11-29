package com.ccdweb.springboot.jpa.meallog;

import lombok.Data;

// 식단 + 혈당 동시에 저장

@Data
public class MealGlucoseLogRequestDTO {
    private String mealDescription;  // 사용자가 입력한 식단 설명
	private Integer glucose;
    // 영양성분 필요시 나중에 추가
    
}
