package com.hansung.likelion.global.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 토큰을 추출
        String token = jwtTokenProvider.resolveToken(request);

        // 토큰이 유효한지 검증
        // 토큰이 null 이 아니고, validateToken 메서드를 통과해야 합니다.
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효하면, 토큰으로부터 인증 정보를 가져옵니다.
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // SecurityContext 에 인증 정보를 저장
            // 이로써 현재 요청은 인증된 사용자에 의해 수행되는 것으로 간주됩니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청을 전달
        // 이 코드가 없으면 필터 체인이 여기서 멈추고, 컨트롤러까지 요청이 도달하지 않습니다.
        filterChain.doFilter(request, response);
    }
}
