package com.ccdweb.springboot.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(name = "user_id",nullable = false, length = 16, unique = true)
	private String userId;
	
	@Column(nullable = false, length = 128)
	private String password;

	@Column(length = 32)
	private String name;
	
    /**
     * 성별 (M = 남, F = 여)
     * DB에서 gender INT CHECK (gender IN (1,2))
     */
    private String gender;
    
    private LocalDate birthDate;
	private Double height;
    private Double weight;
    
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable=false)
    private String role = "USER";
    
}
