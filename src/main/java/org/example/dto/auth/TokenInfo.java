package org.example.dto.auth;

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
