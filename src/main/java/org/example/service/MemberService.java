package org.example.service;

import org.example.common.exception.PasswordNotMatchingException;
import org.example.dto.login.LoginRequestDto;
import org.example.dto.login.TokenInfo;
import org.example.dto.register.EmailRequestDto;
import org.example.dto.register.RegisterRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface MemberService {

    public void register(RegisterRequestDto registerRequestDto);
    public void emailExist(EmailRequestDto emailRequestDto);
    public TokenInfo login(LoginRequestDto loginRequestDto) throws PasswordNotMatchingException;
}
