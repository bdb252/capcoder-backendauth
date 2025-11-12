package com.ccdweb.springboot.geminiapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.util.Base64;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService geminiService;

    @PostMapping("/imagedb")
    public ResponseEntity<?> gemini(@RequestParam("image") MultipartFile image) {
        try {
        	// 이미지 파일을 Base64로 인코딩
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // MIME 타입 가져오기 (예: image/jpeg, image/png)
            String mimeType = image.getContentType();
            
//            return ResponseEntity.ok().body(geminiService.getContents(
        	return ResponseEntity.ok().body(geminiService.getContentsWithImage(
                "이 음식 사진의 이름 'meal_description'과 일반적인 영양 성분 'calorie', 'total_carb', 'sugar', 'protein', 'total_fat'를 계산해서 알려줘"+
            		"100자 이내로 짧게 영양 성분이 포함되게 응답해줘. json형식으로 응답해줘.",
            		base64Image,
            		mimeType));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("이미지 처리 중 오류 발생: " + e.getMessage());
        }
    }
    
}
