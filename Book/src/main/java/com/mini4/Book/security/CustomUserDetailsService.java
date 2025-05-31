package com.mini4.Book.security;

import com.mini4.Book.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final com.mini4.Book.repository.UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 사용자 이메일로 User 엔티티를 찾고, CustomUserDetails로 변환하여 반환
        return userRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다: " + email));
    }

    // User 엔티티를 CustomUserDetails 객체로 변환하는 헬퍼 메서드
    private UserDetails createUserDetails(User user) {
        return new CustomUserDetails(user);
    }
}