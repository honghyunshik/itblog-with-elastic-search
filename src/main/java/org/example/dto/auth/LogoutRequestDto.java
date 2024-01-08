package org.example.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.logout.Logout;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LogoutRequestDto {
    private String token;
    private LocalDateTime expiration;

    public Logout toEntity(){
        return Logout.builder()
                .token(this.token)
                .expiration(this.expiration)
                .build();
    }
}
