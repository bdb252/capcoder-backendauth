package com.ccdweb.springboot.geminiapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService geminiService;

    @GetMapping("/imagedb")
    public ResponseEntity<?> gemini() {
        try {
            return ResponseEntity.ok().body(geminiService.getContents(
                "피자의 일반적인 영양 성분 'calorie', 'total_carb', 'sugar', 'protein', 'total_fat'를 계산해서 알려줘"+
            		"100자 이내로 짧게 영양 성분이 포함되게 응답해줘. json형식으로 응답해줘."));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
