package org.example.dto.login;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokenInfo {

    private String grantType;
    private String accessToken;
    private String refreshToken;
}
