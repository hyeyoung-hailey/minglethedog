package com.example.minglethedog.dto;

import com.example.minglethedog.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security에서 제공하는 표준 인터페이스 UserDetails를 구현해 사용자의 인증 및 권한 관리를 수행한다.
 * CustomUserDetails를 구현해 사용자 정의 로직을 추가했음.
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {return user.getUsername();}

    public Long getUserId() {return user.getId();}


}
