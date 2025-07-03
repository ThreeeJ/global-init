package com.hansung.likelion.global.auth.jwt;

import com.hansung.likelion.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 헤더 이름과 접두사를 상수로 정의
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final Key key;
    private final long validityInMilliseconds;
    private final MyUserDetailsService userDetailsService;

    // 생성자 주입
    public JwtTokenProvider(@Value("${security.jwt.secret-key}") String secretKey,
                            @Value("${security.jwt.expiration}") long validityInMilliseconds,
                            MyUserDetailsService userDetailsService
    ) {
        // 단순한 '바이트 덩어리'를 JWT 서명에 사용할 수 있는 안전하고 규격에 맞는 '암호화 키' 객체로 변환하는 과정
            // openssl 이나 온라인 생성기로 키를 만들었다면, 그 키는 "Base64로 인코딩된 문자열"
            // Base64를 "디코딩(decode)"하여 원본 바이트 배열을 얻어야 함.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            // 인코딩 된 문자열이 아니라면, 주입받은 비밀 키 문자열을 바이트 배열로 변환
        // byte[] keyBytes = secretKey.getBytes();

            // 변환된 바이트 배열을 사용하여 "HMAC-SHA 알고리즘"에 사용할 키 객체를 생성합니다.
        this.key = Keys.hmacShaKeyFor(keyBytes);

        this.validityInMilliseconds = validityInMilliseconds;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 사용자 ID를 받아 JWT 토큰을 생성합니다.
     * @param userId 사용자 ID
     * @return 생성된 JWT 토큰 문자열
     */
    public String createToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.validityInMilliseconds);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 토큰의 주체(subject)로 사용자 ID를 문자열로 설정
                .setIssuedAt(now)                   // 토큰 발급 시간을 현재 시간으로 설정
                .setExpiration(validity)            // 토큰 만료 시간을 설정
                .signWith(key)                      // 준비된 key 를 사용하여 토큰을 서명 (알고리즘은 key 에 이미 포함됨)
                .compact();                         // 최종적으로 압축된 JWT 문자열을 생성
    }

    /**
     * 토큰 문자열을 파싱하여 클레임(토큰에 담긴 정보)을 추출합니다.
     * 서명 검증에 실패하거나 토큰이 유효하지 않으면 예외가 발생합니다.
     * @param token JWT 토큰 문자열
     * @return 토큰의 클레임 정보
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 서명 검증에 사용할 키를 설정합니다.
                .build()
                .parseClaimsJws(token) // 토큰을 파싱하고 서명을 검증합니다.
                // 서명 검증, 만료 시간, 형식 검증, 다양한 기본적인 검사들 수행
                .getBody(); // Payload(Claims) 부분을 반환합니다.
    }

    /**
     * JWT 토큰을 복호화하여 토큰에 들어있는 정보를 기반으로 Authentication 객체를 반환합니다.
     * 데이터베이스에서 사용자 정보를 조회하여 SecurityContext 에 저장할 완전한 형태의 인증 객체를 생성합니다.
     * @param token JWT 토큰
     * @return 사용자 정보를 담은 Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        // 토큰에서 클레임(정보) 추출
        Claims claims = getClaims(token);

        // 클레임에서 사용자 ID(Subject) 추출
        String userId = claims.getSubject();

        // 사용자 ID를 기반으로 데이터베이스에서 UserDetails 객체 조회
        // 이 UserDetails 는 사용자의 아이디, 패스워드, 권한 등을 담고 있는 Spring Security 의 표준 인터페이스
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        // UserDetails 객체, 비밀번호(보통 null 처리), 권한 정보를 기반으로 Authentication 객체 생성
        // 이 객체는 Spring Security 가 내부적으로 사용자의 인증 상태를 관리하는 데 사용됩니다.
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * HTTP 요청의 헤더에서 Bearer 토큰을 추출합니다.
     * @param request HttpServletRequest 객체
     * @return 추출된 JWT 문자열 또는 토큰이 없을 경우 null
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // StringUtils.hasText(bearerToken) : 빈 문자열(""), 공백(" ")을 모두 한번에 검사
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 토큰이 유효한지 검증하는 메서드
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            // getClaims 가 내부적으로 토큰을 파싱하고 서명을 검증합니다.
            // 유효하지 않으면 예외가 발생하므로, catch 블록으로 잡힙니다.
            getClaims(token);
            return true;
        } catch (Exception e) {
            // 모든 JwtException 의 상위 예외 또는 구체적인 예외들을 잡아 처리
            // log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
