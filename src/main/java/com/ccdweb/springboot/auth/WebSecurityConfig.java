package com.ccdweb.springboot.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ccdweb.springboot.jpa.UserService;
import com.ccdweb.springboot.jwt.JwtAuthFilter;
import com.ccdweb.springboot.jwt.JwtUtil;

import jakarta.servlet.DispatcherType;

@Configuration
public class WebSecurityConfig {
	@Autowired
	private UserService userService;
	
	@Autowired
	private MyAuthFailureHandler myAuthFailureHandler;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http.csrf((csrf) -> csrf.disable())
			.cors((cors) -> {})
			
			// 세션 사용 안함(JWT 기반)
			.sessionManagement((session) -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			
			.authorizeHttpRequests((request) -> request
					.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
					// .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
					// .requestMatchers("/index.do", "/login.do", "/regist.do","/myError.do").permitAll()
					// .requestMatchers("/error").permitAll()
					// .requestMatchers("/gemini/imagedb").permitAll()
					// .requestMatchers("/mypage.do", "/member/mypage.do").permitAll()
					.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					.requestMatchers("/api/member/**").permitAll()
					.anyRequest().permitAll()
					// .anyRequest().authenticated()
			);
		
		// JWT 필터 추가
		http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

				
		http.formLogin((formLogin) -> formLogin.disable());
//		http.formLogin((formLogin) -> formLogin
//				.loginPage("/login.do") //view 
//				.loginProcessingUrl("/api/member/loginAction.do") //api
//				.failureHandler(myAuthFailureHandler)
//				.usernameParameter("userId") //default : username
//				.passwordParameter("password") //default : password
//				.defaultSuccessUrl("/index.do", true)
//				.failureUrl("/myError.do") //default : /login?error
//				.permitAll());

		http.logout((logout) -> logout.disable());
				// .logoutUrl("/api/member/logout.do")
				// .logoutSuccessUrl("/")
				// .permitAll());
		
		http.exceptionHandling((exceptionHandling) -> exceptionHandling
	            .authenticationEntryPoint((req, res, e) -> res.sendError(401, "Unauthorized"))
        		.accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
		
		return http.build();
	}
	
	@Bean
	public JwtAuthFilter jwtAuthFilter() {
		JwtAuthFilter filter = new JwtAuthFilter();
		filter.setJwtUtil(jwtUtil);
		return filter;
	}
	
	@Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
}