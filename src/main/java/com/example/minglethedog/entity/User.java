package com.example.minglethedog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    //비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }

    //현재 저장된 비밀번호와 입력된 비밀번호 일치하는지 확인
    public boolean isPasswordMatch(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, password);
    }

}
