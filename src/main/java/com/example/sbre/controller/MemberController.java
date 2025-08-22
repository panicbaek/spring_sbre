package com.example.sbre.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.sbre.JWT.JwtService;
import com.example.sbre.Service.MemberService;
import com.example.sbre.domain.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody Member member) {
		memberService.insert(member);
		
		return new ResponseEntity<>("회원가입 성공!", HttpStatus.OK);
	}
	
	// 로그인요청시 만드는 토큰 메서드
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Member member) {
		
		UsernamePasswordAuthenticationToken cred = 
				new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());
		
		// 인증 성공한 사람이 auth 객체에 담김 (로그인을 성공한 사람)
		Authentication auth = authenticationManager.authenticate(cred); // Manager한테 인증 하는 코드
		// jwt에는 토큰이 담겨있음
		String jwt = jwtService.createToken(auth.getName(), auth.getAuthorities());
		
		return ResponseEntity.ok()
							 .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt) // header에 토큰을 심어줘야함
							 .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization") // crossOrigin 때문에 헤더를 못읽을 수도 있어서 허용하라는 코드
							 .build();
	}
	
	@GetMapping("/userinfo")
	public ResponseEntity<?> userInfo(Authentication auth) {
		
		Member member = memberService.getMember(auth.getName());
		
		return new ResponseEntity<>(member, HttpStatus.OK);
	}
	
	
}
