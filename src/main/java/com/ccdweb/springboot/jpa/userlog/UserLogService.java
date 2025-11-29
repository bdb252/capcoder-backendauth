package com.ccdweb.springboot.jpa.userlog;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccdweb.springboot.jpa.UserEntity;
import com.ccdweb.springboot.jpa.UserRepository;

@Service
public class UserLogService {
	
	@Autowired
	private UserLogRepository userLogRepository;
	
	@Autowired
    private UserRepository userRepository;
	
	// 특정 사용자의 혈당 로그 조회
	public List<UserLogEntity> getUserLogsMonth(String userId, int year, int month) {
		YearMonth ym = YearMonth.of(year, month);

		// 1일 00:00:00
        LocalDateTime start = ym.atDay(1).atStartOfDay();         
        // 말일 23:59:59.999...
        LocalDateTime end   = ym.atEndOfMonth().atTime(LocalTime.MAX); 

		return userLogRepository.findByUser_UserIdAndLogTimeBetween(userId, start, end);
	}
	
	@Transactional
    public UserLogEntity saveUserLog(String userId, Integer glucose) {
        // 1) 유저 엔티티 조회
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2) 로그 엔티티 생성
        UserLogEntity log = new UserLogEntity();
        log.setUser(user);
        log.setGlucose(glucose);

        // logTime은 DB default로 넣게 해둔 상태 (insertable = false)

        // 3) 저장
        return userLogRepository.save(log);
    }

    // 기록 삭제
    @Transactional
    public void deleteUserLog(String userId, UUID logId){
        UserLogEntity log = userLogRepository.findByLogidAndUserUserId(logId, userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 기록이 없습니다."));
        userLogRepository.delete(log);
    }
}
