package com.ccdweb.springboot.jpa;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String userId;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private Double height;
    private Double weight;

    public static UserInfoDTO fromEntity(UserEntity entity) {
        return UserInfoDTO.builder()
                .userId(entity.getUserId())
                .name(entity.getName())
                .gender(entity.getGender())
                .birthDate(entity.getBirthDate())
                .height(entity.getHeight())
                .weight(entity.getWeight())
                .build();
    }
}
