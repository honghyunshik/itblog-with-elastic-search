package org.example.dto.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.example.domain.member.Member;
import org.example.domain.member.Role;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

    @NotNull(message = "생일은 필수 입력사항입니다")
    @DateTimeFormat
    private LocalDate birth;

    @NotNull(message = "이메일은 필수 입력사항입니다")
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;

    @NotBlank
    @NotNull(message = "이름은 필수 입력사항입니다")
    @Pattern(regexp = "^[가-힣]{2,4}$", message = "이름은 최소 두글자 이상, 최대 네글자 이하의 한글로만 가능합니다")
    private String name;

    @NotBlank
    @NotNull(message = "비밀번호는 필수 입력사항입니다")
    @Pattern(regexp = "^[a-zA-Z\\d`~!@#$%^&*()-_=+]{8,12}$")
    private String password;

    @NotNull(message = "성별은 필수 입력사항입니다")
    private String gender;


    public Member toEntity(){
        return Member.builder()
                .birth(this.birth)
                .email(this.email)
                .name(this.name)
                .gender(this.gender)
                .password(this.password)
                .role(Role.USER)
                .build();
    }
}
