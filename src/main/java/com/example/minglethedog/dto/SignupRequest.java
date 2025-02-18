package com.example.minglethedog.dto;

import com.example.minglethedog.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20, message = "유저네임은 3글자이상 20글자 이하만 가능합니다.")
    private String username;
    @NotBlank
    private String password;
    private Role role;
}
