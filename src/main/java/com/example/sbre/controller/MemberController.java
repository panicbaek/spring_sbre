package com.example.sbre.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.sbre.Service.MemberService;
import com.example.sbre.domain.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody Member member) {
		memberService.insert(member);
		
		return new ResponseEntity<>("회원가입 성공!", HttpStatus.OK);
	}
	
}
