package org.example.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginStatusResponseDto {
    //login true, logout false
    private boolean status;
    private String email;
}
