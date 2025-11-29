package com.ccdweb.springboot.jpa;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccdweb.springboot.jpa.meallog.MealGlucoseLogRequestDTO;
import com.ccdweb.springboot.jpa.meallog.MealGlucoseLogResponseDTO;
import com.ccdweb.springboot.jpa.meallog.MealGlucoseLogService;
import com.ccdweb.springboot.jpa.userlog.UserLogEntity;
import com.ccdweb.springboot.jpa.userlog.UserLogResponseDTO;
import com.ccdweb.springboot.jpa.userlog.UserLogService;
import com.ccdweb.springboot.jwt.JwtUtil;

@RestController
@RequestMapping("/api")
public class UserController {
	@Autowired
	UserService userService;

	@Autowired
	private UserRepository repo;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserLogService userLogService;

	@Autowired
	private MealGlucoseLogService mealGlucoseLogService;

	// 중복확인
	@PostMapping("/member/checkId")
	public Map<String, Boolean> checkDuplicateId(@RequestParam("userId") String userId) {
		boolean exists = userService.existsByUserId(userId);
		Map<String, Boolean> response = new HashMap<>();
		response.put("exists", exists);
		response.put("available", !exists); // 추가: 사용 가능 여부
		return response;
	}

	// 회원가입
	@PostMapping("/member/regist.do")
	public ResponseEntity<?> insert(@RequestBody UserEntity member) {
		try {

			userService.insertMember(member);
			Map<String, String> response = new HashMap<>();
			response.put("message", "회원가입이 완료되었습니다.");
			response.put("userId", member.getUserId());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "회원가입 실패");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}

	// 로그인
	@PostMapping("/member/loginAction.do")
	public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
		try {
			String userId = loginData.get("userId");
			String password = loginData.get("password");

			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(userId, password));

			// 사용자 정보 가져오기
			UserEntity user = userService.findByUserId(userId);

			// JWT 토큰 생성
			String token = jwtUtil.generateToken(user.getUserId(), user.getRole());

			Map<String, Object> response = new HashMap<>();
			response.put("token", token);
			response.put("userId", user.getUserId());
			response.put("name", user.getName());
			response.put("role", user.getRole());

			return ResponseEntity.ok(response);

		} catch (AuthenticationException e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "로그인 실패");
			error.put("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}
	}

	// 로그인한 사용자의 DB 정보 조회
	@GetMapping("/member/userInfo.do")
	public ResponseEntity<UserInfoDTO> getUserInfo(Authentication auth) {
		if (auth == null) {
			return ResponseEntity.status(401).build();
		}

		String userId = (String) auth.getPrincipal();
		UserInfoDTO userInfo = userService.getUserInfo(userId);

		return ResponseEntity.ok(userInfo);
	}

	// 마이페이지
	@PostMapping("/member/mypage.do")
	public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> profileData, 
			Authentication auth) {

		try {
			UserEntity member = repo.findByUserId(auth.getName())
					.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

			// null 체크 후 업데이트
			if (profileData.containsKey("name")) {
				member.setName((String) profileData.get("name"));
			}
			if (profileData.containsKey("gender")) {
				member.setGender((String) profileData.get("gender"));
			}
			if (profileData.containsKey("height")) {
				member.setHeight(((Number) profileData.get("height")).doubleValue());
			}
			if (profileData.containsKey("weight")) {
				member.setWeight(((Number) profileData.get("weight")).doubleValue());
			}
			if (profileData.containsKey("birthDate")) {
				member.setBirthDate(LocalDate.parse((String) profileData.get("birthDate")));
			}

			repo.save(member);

			Map<String, Object> response = new HashMap<>();
			response.put("message", "프로필이 업데이트되었습니다.");
			response.put("user", member);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "프로필 업데이트 실패");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}

	// 개별 혈당 조회
	@GetMapping("/my/glucoseLog.do")
	public ResponseEntity<List<UserLogResponseDTO>> getMyLogs(
			Authentication auth,
			@RequestParam(name = "year", required = false) Integer year,
			@RequestParam(name = "month", required = false) Integer month) {

		if (auth == null) {
			return ResponseEntity.status(401).build();
		}

		String userId = (String) auth.getPrincipal();

		LocalDate today = LocalDate.now();
		int y = (year != null) ? year : today.getYear();
		int m = (month != null) ? month : today.getMonthValue();

		List<UserLogEntity> logs = userLogService.getUserLogsMonth(userId, y, m);

		List<UserLogResponseDTO> result = logs.stream().map(UserLogResponseDTO::fromEntity).toList();

		return ResponseEntity.ok(result);
	}

	// 식단 + 혈당 저장 -> MealGlucoseLogService
	@PostMapping("/my/saveGlucose.do")
	public ResponseEntity<?> addUserLog(Authentication auth,
			@RequestBody MealGlucoseLogRequestDTO dto) {

		if (auth == null) {
			return ResponseEntity.status(401).build();
		}

		String userId = (String) auth.getPrincipal();

		MealGlucoseLogResponseDTO response =
                mealGlucoseLogService.saveMealAndGlucose(userId, dto);
		
		return ResponseEntity.ok(response);
	}
	
	// 혈당 데이터 삭제 -> MealGlucoseLogService
	@DeleteMapping("/my/delete.do")
	public ResponseEntity<?> delete(Authentication auth,
			@RequestParam("logId") UUID logId) {

		if (auth == null) {
			return ResponseEntity.status(401).build();
		}

		String userId = (String) auth.getPrincipal();

		mealGlucoseLogService.deleteLogs(userId, logId);

		return ResponseEntity.ok("삭제되었습니다.");
	}

}
