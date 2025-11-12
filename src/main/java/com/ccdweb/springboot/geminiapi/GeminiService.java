package com.ccdweb.springboot.geminiapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeminiService {
    @Qualifier("geminiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // 텍스트만 전송
    public String getContents(String prompt) {

        // Gemini에 요청 전송
        String requestUrl = apiUrl + "?key=" + geminiApiKey;

        RequestDTO request = new RequestDTO(prompt);
        ResponseDTO response = restTemplate.postForObject(requestUrl, request, ResponseDTO.class);

        String message = response.getCandidates().get(0).getContent().getParts().get(0).getText().toString();

        return message;
    }
    
    // 텍스트 + 이미지 전송
    public String getContentsWithImage(String prompt, String base64Image, String mimeType) {
        // Gemini에 요청 전송
        String requestUrl = apiUrl + "?key=" + geminiApiKey;

        // 텍스트와 이미지를 포함한 요청 생성
        RequestDTO request = new RequestDTO(prompt, base64Image, mimeType);
        ResponseDTO response = restTemplate.postForObject(requestUrl, request, ResponseDTO.class);

        String message = response.getCandidates().get(0).getContent().getParts().get(0).getText().toString();

        return message;
    }
}
