package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.constants.JwtConstants;
import org.example.dto.login.LoginRequestDto;
import org.example.dto.login.TokenInfo;
import org.example.dto.register.EmailRequestDto;
import org.example.dto.register.RegisterRequestDto;
import org.example.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RequestMapping(value = "/api/v1/member")
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto,Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(Collections.singletonMap("errors", errors.getAllErrors()));
        }
        TokenInfo tokenInfo = memberService.login(loginRequestDto);
        return ResponseEntity.ok().header(JwtConstants.TYPE,tokenInfo.getAccessToken()).build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequestDto, Errors errors){

        //회원가입 Dto 유효성 검사 실패시 bad request + error 내용 반환
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(Collections.singletonMap("errors", errors.getAllErrors()));
        }

        memberService.register(registerRequestDto);

        return ResponseEntity.ok().body(Collections.singletonMap("message","회원가입이 완료되었습니다"));
    }

    @PostMapping("/email")
    public ResponseEntity<?> emailCheck(@Valid @RequestBody EmailRequestDto emailRequestDto, Errors errors){

        //이메일 유효성 검사 실패시 bad request + error 내용 반환
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("errors", errors.getAllErrors()));
        }

        memberService.emailExist(emailRequestDto);

        return ResponseEntity.ok().body(Collections.singletonMap("message","중복된 이메일이 없습니다"));
    }
}
