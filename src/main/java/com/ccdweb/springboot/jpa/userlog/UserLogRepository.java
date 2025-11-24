package com.ccdweb.springboot.jpa.userlog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends JpaRepository<UserLogEntity, UUID> {
	List<UserLogEntity> findByUser_UserIdAndLogTimeBetween(String userId, LocalDateTime start, LocalDateTime end);
}
