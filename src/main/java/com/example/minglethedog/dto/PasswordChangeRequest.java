package com.example.minglethedog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {

    @NotBlank(message = "현재 비밀번호는 필수 입력 사항입니다.")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호는 필수 입력 사항입니다.")
    private String newPassword;
}
