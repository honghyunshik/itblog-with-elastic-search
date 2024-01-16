package org.example.service;

import org.example.common.exception.member.PasswordNotMatchingException;
import org.example.dto.auth.LoginRequestDto;
import org.example.dto.auth.TokenInfo;
import org.example.dto.register.RegisterRequestDto;

public interface MemberService {

    public void register(RegisterRequestDto registerRequestDto);
    public void emailExist(String email);
    public TokenInfo login(LoginRequestDto loginRequestDto) throws PasswordNotMatchingException;
    public int createRandomCodeForEmailAuthentication();
}
