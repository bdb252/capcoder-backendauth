package com.ccdweb.springboot.jpa;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccdweb.springboot.jpa.userlog.UserLogEntity;
import com.ccdweb.springboot.jpa.userlog.UserLogRequestDTO;
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
	public ResponseEntity<UserInfoDTO> getUserInfo(Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.status(401).build();
		}

		String userId = (String) authentication.getPrincipal();
		UserInfoDTO userInfo = userService.getUserInfo(userId);

		return ResponseEntity.ok(userInfo);
	}

	// 마이페이지
	@PostMapping("/member/mypage.do")
	public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> profileData, // 변경: JSON 요청 처리
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

	// userLog table에 개인 혈당 입력
	@PostMapping("/my/glucoseAdd.do")
	public ResponseEntity<?> addUserLog(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserLogRequestDTO request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String userId = userDetails.getUsername(); // JWT에서 온 userId
        Integer glucose = request.getGlucose();

        if (glucose == null) {
            return ResponseEntity.badRequest().body("혈당값(glucose)은 필수입니다.");
        }

        UserLogEntity saved = userLogService.saveUserLog(userId, glucose);

        return ResponseEntity.ok(saved);  // 바로 엔티티 반환 (나중에 DTO로 바꿔도 됨)
    }
	
	// 개별 혈당 조회
	@GetMapping("/my/glucoseLog.do")
	public ResponseEntity<List<UserLogEntity>> getMyLogs(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
			) {

		if (userDetails == null)
			return ResponseEntity.status(401).build();

		String userId = userDetails.getUsername();
		LocalDate today = LocalDate.now();
        int y = (year != null) ? year : today.getYear();
        int m = (month != null) ? month : today.getMonthValue();

        List<UserLogEntity> logs = userLogService.getUserLogsMonth(userId, y, m);
        return ResponseEntity.ok(logs);
	}
	
//	//전체조회
//	@GetMapping("/selectAll.do")
//	public String selectAll(Model model) {
//	}
//	
//	//삭제
//	@GetMapping("/delete.do")
//	public String delete(@RequestParam("id")Long p_id) {
//	}
//	
//	//수정
//	@GetMapping("/update.do")
//	public String update(MemberEntity memberTable) {
//	}
}
