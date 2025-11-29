package com.ccdweb.springboot.jpa.userlog;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLogService {
	
	@Autowired
	private UserLogRepository userLogRepository;
	
	
	// 특정 사용자의 혈당 로그 조회
	public List<UserLogEntity> getUserLogsMonth(String userId, int year, int month) {
		YearMonth ym = YearMonth.of(year, month);

		// 1일 00:00:00
        LocalDateTime start = ym.atDay(1).atStartOfDay();         
        // 말일 23:59:59.999...
        LocalDateTime end   = ym.atEndOfMonth().atTime(LocalTime.MAX); 

		return userLogRepository.findByUser_UserIdAndLogTimeBetween(userId, start, end);
	}

}
