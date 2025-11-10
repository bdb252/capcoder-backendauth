package com.ccdweb.springboot.geminiapi;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDTO {

    // API 요청에 포함될 대화 내용 리스트
    private List<Content> contents;
    // 응답 생성 시 사용될 설정값
    private GenerationConfig generationConfig;

    @Getter @Setter
    public static class Content {
        // 실제 텍스트 내용을 담는 객체
        private Parts parts;
    }

    @Getter @Setter
    public static class Parts {
        // 전송할 텍스트 메시지
        private String text;
        // 이미지

    }

    @Getter @Setter
    public static class GenerationConfig {
        // 생성할 응답 후보의 개수
        private int candidate_count;
        // 생성할 최대 토큰 수(응답 길이 제한)
        private int max_output_tokens;
        // 응답의 창의성/무작위성 정도(0.0~1.0), 낮을수록 일관적, 높을수록 창의적
        private double temperature;

    }

    public RequestDTO(String prompt) {
        // contents 리스트 초기화
        this.contents = new ArrayList<>();
        // Content 객체 생성 및 설정
        Content content = new Content();
        Parts parts = new Parts();

        // 프롬프트 텍스트/이미지를 Parts에 설정
        parts.setText(prompt);
        content.setParts(parts);

        // contents 리스트에 추가
        this.contents.add(content);

        // 기본 생성 설정값 초기화
        this.generationConfig = new GenerationConfig();
        this.generationConfig.setCandidate_count(1);
        this.generationConfig.setMax_output_tokens(100);
        this.generationConfig.setTemperature(0.7);
    }
}