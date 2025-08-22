package com.example.sbre.JWT;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtService { // JWT를 이용해서 로그인에 성공하면 토큰을 생성해주는 클래스
	
	// 토큰의 만료 시간 저장 변수 ( 단위 : ms )
	static final long EXPIRATIONTIME = 24 * 60 * 60 * 1000;
	
	// 토큰 타입 ( 응답 헤더에 사용될 접두어 )
	static final String PREFIX = "Bearer "; // Bearer 은 고정값임
	
	// 토큰 임시 서명 키
	static final SecretKey KEY = Jwts.SIG.HS256.key().build();
	
	// 권한 관련 클레임 키
	static final String ROLES_CLAIM = "roles";

	// 토큰을 생성하는 메서드
	public String createToken(String username, Collection<? extends GrantedAuthority> authorities ) {
		
		Date now = new Date(); // 토큰이 발급되는 시점
		Date exp = new Date( now.getTime() + EXPIRATIONTIME); // 토큰이 만료되는 시점
		
		// 권한들을 담은 컬렉션
		List<String> roles = (authorities == null) 
				? List.of() 
				: authorities.stream().map(GrantedAuthority::getAuthority).toList(); 
		
		return Jwts.builder()
				.subject(username)	 // sub
				.issuedAt(now)	   	 // iat
				.expiration(exp)	// exp
				.signWith(KEY)		// 서명
				.claim(ROLES_CLAIM, roles)	// 커스텀 클레임
				.compact(); // 마지막에 compact를 넣으면 토큰설정 끝
	}
	
	/**
	 * 요청 객체에서 헤더에 Authorization에 있는 토큰을 추출하는 메서드
	 * @param 요청객체 넣으셈
	 */
	public String resolveToken(HttpServletRequest request) { // HttpServletRequest 요청객체임
		
		// 응답헤더 HttpServletRequest 토큰값을 찾은다음 꺼내서 header변수에 담음
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		// 토큰이 있냐 없냐 
		if(header != null && header.startsWith(PREFIX)) {
			// 있으면 토큰만 뽑아내는 메서드임 
			return header.substring(PREFIX.length()).trim();
		}
		
		return null;
	}
	
	/**
	 * 토큰 유효성 검사하는 메서드
	 */
	public boolean validate(String token) {
		if(token == null || token.isBlank())
			return false;
		
		//예외 발생시 
		try { 
			Jwts.parser()
				.verifyWith(KEY) // 서명 지정
				.clockSkewSeconds(30) // 30초정도 오차는 허용 
				.build()
				.parseSignedClaims(token); // 검증
			
			return true;
		} catch (ExpiredJwtException e) {
			return false; // 토큰 만료 시
		} catch (JwtException e) {
			return false; // 서명, 형식, 등등 검증실패 시
		} catch (Exception e) {
			return false; // 기타 예외들
		}
	}
	/*
	 * 토큰에서 subject(username) 꺼내주는 메서드
	 */
	public String getUsername(String token) {
		Claims claims = Jwts.parser()
							.verifyWith(KEY)
							.build()
							.parseSignedClaims(token)
							.getPayload(); // 토큰의 내용을 꺼내옴
		return claims.getSubject();
	}
	/*
	 * 토큰에서 권한(role)을 꺼내주는 메서드
	 */
	public List<? extends GrantedAuthority> getAuthorities(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(KEY)
				.build()
				.parseSignedClaims(token)
				.getPayload(); // 토큰의 내용을 꺼내옴
		Object raw = claims.get(ROLES_CLAIM); // 토큰의 권한을 꺼내옴
		
		if(raw instanceof List<?> list) { // raw가 List형태임?
			return list.stream()
					.filter(String.class::isInstance) // true만 남기고 false는 삭제해
					.map(String.class::cast) // 트루면 형변환 시켜줘
					.map(SimpleGrantedAuthority::new) // 형변환 됐으면 권한 객체로 만들어줘
					.collect(Collectors.toList()); // 이제 리스트형태로 만들어줘
		}
		
		return List.of();
	}
	
}
