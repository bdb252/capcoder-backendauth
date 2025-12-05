package com.ccdweb.springboot.jpa.meallog;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopKMealDTO {
    private String mealName;
    private long count;

}
