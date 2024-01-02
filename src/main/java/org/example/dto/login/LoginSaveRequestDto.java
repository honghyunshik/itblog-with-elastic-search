package org.example.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.example.domain.login.Login;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginSaveRequestDto {

    private String email;
    private Date expiration;

    public Login toEntity(){
        return Login.builder()
                .email(this.email)
                .expiration(this.expiration)
                .build();
    }
}
