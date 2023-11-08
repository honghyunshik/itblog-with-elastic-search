package org.example.service;

import org.example.dto.register.EmailRequestDto;
import org.example.dto.register.RegisterRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface MemberService {

    public void register(RegisterRequestDto registerRequestDto);
    public ResponseEntity<?> emailCheck(EmailRequestDto emailRequestDto);
}
