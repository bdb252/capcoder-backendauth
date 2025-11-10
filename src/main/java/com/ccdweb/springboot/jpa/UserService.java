package com.ccdweb.springboot.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	// 비밀번호 해싱
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException(userId + " 사용자를 찾을 수 없습니다."));
		
		return User.builder() 
				.username(userEntity.getUserId()) // 사용자 ID
				.password(userEntity.getPassword()) // DB에서 가져온 해시된 비밀번호
				.roles(userEntity.getRole()) // 사용자 권한 (예: "USER" 또는 "ADMIN")
				.build();
	}
	// 중복확인
	public boolean existsByUserId(String userId) {
		return userRepository.existsByUserId(userId);
	}
	
	// 입력
	public void insertMember(UserEntity member) {
		String rawpw = member.getPassword();
		String encodedpw = passwordEncoder.encode(rawpw);
		member.setPassword(encodedpw);
		System.out.println("비밀번호: "+rawpw+", 해싱된 비번: "+encodedpw);
		if (member.getRole() == null || member.getRole().isBlank()) {
			member.setRole("USER");
		}
		userRepository.save(member);
	}

	// 조회(개인)
	public UserEntity findByUserId(String userId) {
		return userRepository.findByUserId(userId)
	            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
	}
//
//	// 수정
//	public void updateMember(UserEntity member) {
//		userRepository.save(member);
//	}
//	
//	// 삭제
//	public void deleteMember(UUID id) {
//		userRepository.deleteById(id);
//	}
}
