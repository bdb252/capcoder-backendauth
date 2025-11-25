package com.ccdweb.springboot.geminiapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import com.ccdweb.springboot.jpa.UserEntity;
import com.ccdweb.springboot.jpa.UserRepository;
import com.ccdweb.springboot.jpa.meallog.MealLogEntity;
import com.ccdweb.springboot.jpa.meallog.MealLogRepository;
import com.ccdweb.springboot.jpa.userlog.UserLogEntity;
import com.ccdweb.springboot.jpa.userlog.UserLogRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService geminiService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private UserLogRepository userLogRepository;
    
    @GetMapping("/recommend")
    public ResponseEntity<?> gemini(@AuthenticationPrincipal String userId) {
    	
    	if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        try {
        	// 1) 사용자 정보 
        	UserEntity user = userRepository.findByUserId(userId)
        			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        	
        	// 2) 가장 최근 식단
        	MealLogEntity lastMeal = mealLogRepository.findTopByUserOrderByLogTimeDesc(user)
        			.orElseThrow(() -> new IllegalArgumentException("최근 식단 기록이 존재하지 않습니다."));
        	
        	// 3) 해당 식단의 혈당 로그
        	UserLogEntity glucose = userLogRepository.findByMeal(lastMeal)
        			.orElseThrow(() -> new IllegalArgumentException("혈당 기록이 없습니다."));
        	
        	// 4) 나이 계산
        	int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();

        	String prompt = "키 : %s"
        			+ "몸무게 : %s"
        			+ "성별 : %s"
        			+ "나이 : %d"
        			+ "이전 식단 : %s"
        			+ "이전 식사 후 2시간이 지났을 때 혈당 : %d"
        			+ "위의 정보를 바탕으로, 혈당이 많이 오르지 않도록 적절한 한끼 식사를 추천해줘.%s"
        			+ "'이전 식단'을 언급하면서 150자 이내로 응답해줘."
        			.formatted(user.getHeight(),
        					user.getWeight(),
        					user.getGender(),
        					age,
        					lastMeal.getMeal_description(),
        					glucose.getGlucose()
        					);
        	System.out.println(prompt);
            return ResponseEntity.ok().body(geminiService.getContents(prompt));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("텍스트 처리 중 오류 발생: " + e.getMessage());
        }
    }
    
    @PostMapping("/imagedb")
    public ResponseEntity<?> gemini_image(@RequestParam("image") MultipartFile image) {
        try {
        	// 이미지 파일을 Base64로 인코딩
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // MIME 타입 가져오기 (예: image/jpeg, image/png)
            String mimeType = image.getContentType();
            
//            return ResponseEntity.ok().body(geminiService.getContents(
        	return ResponseEntity.ok().body(geminiService.getContentsWithImage(
                "이 음식 사진을 분석해서 음식 이름을 'meal_description'로, 일반적인 영양 성분을 'calorie', 'total_carb', 'sugar', 'protein', 'total_fat'로 계산해서 알려줘"+
        		"사진에 음식이 여러개 있으면 모두 분석해서 json형식으로 응답해줘.",
            		base64Image,
            		mimeType));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("이미지 처리 중 오류 발생: " + e.getMessage());
        }
    }
    
}
