package org.example.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotNull(message = "이메일은 필수 입력사항입니다")
    @NotBlank
    @Email(message = "이메일 양식을 지켜주세요")
    private String email;

    @NotBlank
    @NotNull(message = "비밀번호는 필수 입력사항입니다")
    @Pattern(regexp = "^[a-zA-Z\\d`~!@#$%^&*()-_=+]{8,12}$")
    private String password;
}