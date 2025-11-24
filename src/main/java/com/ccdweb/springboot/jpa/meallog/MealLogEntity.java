package com.ccdweb.springboot.jpa.meallog;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ccdweb.springboot.jpa.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "meal_log")
public class MealLogEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "meal_id")
	private UUID mealid;
	
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name = "user_id",referencedColumnName = "user_id", nullable = false)
	private UserEntity user;
	
	@Column(name = "meal_description", length = 200, nullable = false)
	private String meal_description;
    
    @Column(name = "log_time")
    private LocalDateTime logTime;
    
    @PrePersist
    public void onCreate() {
        if (logTime == null) {
            logTime = LocalDateTime.now();
        }
    }
}