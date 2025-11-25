package com.ccdweb.springboot.jpa.userlog;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ccdweb.springboot.jpa.UserEntity;
import com.ccdweb.springboot.jpa.meallog.MealLogEntity;

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
@NoArgsConstructor
@Entity
@Table(name = "user_log")
public class UserLogEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "log_id")
	private UUID logid;
	
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name = "user_id",referencedColumnName = "user_id", nullable = false)
	private UserEntity user;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", referencedColumnName = "meal_id", nullable = true)
    private MealLogEntity meal;
	
	private Integer glucose;
    
    @Column(name = "log_time")
    private LocalDateTime logTime;
    
    @PrePersist
    public void onCreate() {
        if (logTime == null) {
            logTime = LocalDateTime.now();
        }
    }
}
