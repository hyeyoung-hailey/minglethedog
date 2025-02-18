package com.example.minglethedog.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,ADMIN,REFRESH;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
