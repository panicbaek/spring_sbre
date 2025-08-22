package com.example.sbre.JWT;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // 빈으로 등록하기 위한 어노테이션
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException { // 요청이 들어왔을때 컨트롤러에 가게해주는 메서드
		
		String jwt = jwtService.resolveToken(request); // 서비스에서 이미 토큰꺼내는 메서드를 만들었었음
		boolean check = jwtService.validate(jwt); // 유효한 토큰인지 아닌지 검사
		
		if(check) {
			String username = jwtService.getUsername(jwt);
			List<? extends GrantedAuthority> roles = jwtService.getAuthorities(jwt);
			
			Authentication auth =  // 인증 객체를 생성하는 코드
					new UsernamePasswordAuthenticationToken(username, null, roles);
			
			SecurityContextHolder.getContext().setAuthentication(auth); // 인증 객체를 시큐리티에 검사
		}
		
		filterChain.doFilter(request, response);
	}

	
	
}
