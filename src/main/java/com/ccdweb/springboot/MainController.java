package com.ccdweb.springboot;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ccdweb.springboot.jpa.UserEntity;
import com.ccdweb.springboot.jpa.UserRepository;

@Controller // 뷰 이름(템플릿)을 리턴할 때는 @RestController 대신 @Controller를 사용합니다.
public class MainController {
	@Autowired
	private UserRepository repo;

	@GetMapping({"/", "/index.do"})
    public String home(Principal principal, Model model, Authentication auth) {
		if (auth != null && auth.isAuthenticated()) {
	        System.out.println("로그인됨(principal): " + principal.getName());
	        System.out.println("로그인됨(auth): " + auth.getName());
	        
	        UserEntity member = repo.findByUserId(auth.getName())
                    .orElse(null);
	        
	        if (member != null) {
                model.addAttribute("userId", member.getUserId());
                model.addAttribute("userName", member.getName());
                model.addAttribute("gender", member.getGender());
                model.addAttribute("height", member.getHeight());
                model.addAttribute("weight", member.getWeight());
                model.addAttribute("birthday", member.getBirthDate());
                
                // 나이 계산 (생년월일이 있는 경우)
//                if (member.getBirthday() != null) {
//                    int age = Period.between(member.getBirthday(), LocalDate.now()).getYears();
//                    model.addAttribute("age", age);
//                }
	        }
	    } else {
	        System.out.println("비로그인 상태");
	    }
	        
        return "index"; 
    }

	@RequestMapping("/login.do")
	public String login(Principal principal, Model model) {
		try {
			String userId = principal.getName();
			model.addAttribute("userId", userId);
		} catch (Exception e) {
			System.out.println(">>>로그인 전");
		}
		return "login";
	}

	@GetMapping("/regist.do")
	public String regist() {
		return "regist";
	}

	@GetMapping("/mypage.do")
	public String mypage(Model model, Authentication auth) {
		if (auth == null || !auth.isAuthenticated()) {
	        return "redirect:/login.do";
	    }
		UserEntity member = repo.findByUserId(auth.getName()).orElseThrow(() -> new IllegalStateException("사용자 없음"));
		model.addAttribute("member", member);
		return "mypage";
	}

	@GetMapping("/myError.do")
	public String myError() {
		return "error";
	}
}
