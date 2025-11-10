package com.ccdweb.springboot.auth;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//자동으로 생성되는 빈임을 명시하는 어노테이션
@Configuration
public class MyAuthFailureHandler implements AuthenticationFailureHandler{
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, 
			HttpServletResponse response,
			org.springframework.security.core.AuthenticationException exception) 
					throws IOException, ServletException {
		
		String errorMsg = "";
		
		if(exception instanceof BadCredentialsException) {
			loginFailureCnt(request.getParameter("userId"));
			errorMsg = "아이디나 비밀번호가 맞지 않습니다. 다시 확인해주세요(1)";
		}
		else if(exception instanceof InternalAuthenticationServiceException){
			loginFailureCnt(request.getParameter("userId"));
			errorMsg = "아이디나 비밀번호가 맞지 않습니다. 다시 확인해주세요(2)";			
		}
		else if(exception instanceof DisabledException) {
			errorMsg = "계정이 비활성화되었습니다. 관리자에게 문의해주세요(3)";						
		}
		else if(exception instanceof CredentialsExpiredException) {
			errorMsg = "비밀번호 유효기간이 만료되었습니다. 관리자에게 문의하세요(4)";
		}
		//에러메시지를 request 영역에 저장한 후 커스텀 로그인 페이지로 포워드한다.
		request.setAttribute("errorMsg", errorMsg);
		request.getRequestDispatcher("/login.do?error").forward(request, response);
	}
	
	//각 업무에 맞게 커스텀해서 사용할 수 있는 메서드 정의
	public void loginFailureCnt(String username) {
		System.out.println("요청 아이디:"+username);
		/* 틀린 횟수 업데이트
		틀린 횟수 조회
		만약 3회 이상 실패했다면 계정 잠금 처리*/
	}
}