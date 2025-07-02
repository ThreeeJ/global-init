package com.hansung.likelion.global.auth.jwt;

import com.hansung.likelion.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // 일단 역할이 정해지 않았음
    }

    @Override
    public String getPassword() {
        // JWT 인증에서는 비밀번호를 사용하지 않으므로 null 반환
        return null;
    }

    @Override
    public String getUsername() {
        // 사용자를 식별하는 고유한 값 (여기서는 ID)을 문자열로 반환합니다.
        return String.valueOf(user.getId());
    }

    // --- 계정 상태 관련 메서드들 (특별한 로직이 없다면 모두 true 반환) ---

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명(비밀번호)이 만료되지 않았음
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화됨
    }
}
