package org.example.dto.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

    @NotNull(message = "생일은 필수 입력사항입니다")
    @NotBlank
    @Pattern(regexp = "(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])")
    private String birth;

    @NotNull(message = "이메일은 필수 입력사항입니다")
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;

    @NotBlank
    @NotNull(message = "이름은 필수 입력사항입니다")
    @Pattern(regexp = "^[가-힣]{2,4}$", message = "이름은 최소 두글자 이상, 최대 네글자 이하의 한글로만 가능합니다")
    private String name;

    @NotBlank
    @NotNull(message = "닉네임은 필수 입력사항입니다")
    @Pattern(regexp = "^[가-힣a-zA-Z\\d]{2,8}$", message = "닉네임은 최소 두글자, 최대 8글자로 특수문자를 포함하지 않습니다")
    private String nickname;

    @NotBlank
    @NotNull(message = "비밀번호는 필수 입력사항입니다")
    @Pattern(regexp = "^[a-zA-Z\\d`~!@#$%^&*()-_=+]{8,12}$")
    private String password;

    @NotNull(message = "성별은 필수 입력사항입니다")
    private String gender;

    @NotBlank
    @NotNull
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "전화번호 양식을 확인해주세요")
    private String phoneNumber;
}
