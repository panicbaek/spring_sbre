package com.example.sbre.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.sbre.Repository.MemberRepository;
import com.example.sbre.domain.Member;
import com.example.sbre.domain.RoleType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // 생성자를 통한 의존성 주입 방법 어노테이션
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	
	public void insert(Member member) {
		member.setPassword(passwordEncoder.encode(member.getPassword())); // password 암호화
		member.setRole(RoleType.USER);
		
		memberRepository.save(member);
	}
	
}
