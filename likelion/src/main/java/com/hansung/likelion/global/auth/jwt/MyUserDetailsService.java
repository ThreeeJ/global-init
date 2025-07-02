package com.hansung.likelion.global.auth.jwt;

import com.hansung.likelion.domain.user.entity.User;
import com.hansung.likelion.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security 의 표준 인터페이스인 UserDetailsService 의 핵심 메서드입니다.
     * 여기서 파라미터인 'username' 은 사용자를 식별하는 고유한 값을 의미하며,
     * 이 프로젝트에서는 JWT 의 subject 에 저장된 사용자 ID(Long)를 문자열로 변환하여 사용합니다.
     *
     * @param userId 사용자를 식별하는 고유 ID (문자열 형태)
     * @return UserDetails 객체 (사용자 정보와 권한 포함)
     * @throws UsernameNotFoundException 해당 ID의 사용자가 없을 경우
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        // DB 에서 사용자 정보 조회
        User user = userRepository.getUserById(Long.parseLong(userId));
        // 조회한 User 객체 전체를 UserPrincipal 로 감싸서 반환
        return new UserPrincipal(user);
    }
}
