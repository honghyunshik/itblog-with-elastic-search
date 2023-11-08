package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequestMapping(value = "/api/v1/member")
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequestDto registerRequestDto, Errors errors){

        memberService.register(registerRequestDto);
    }

    @PostMapping("/email")
    public ResponseEntity<?> emailCheck(@Valid @RequestBody EmailRequestDto emailRequestDto, Errors errors){

        //이메일 유효성 검사 실패시 bad request + error 내용 반환
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("errors", errors.getAllErrors()));
        }

        if(memberService.emailExist(emailRequestDto)) return ResponseEntity.badRequest()
                .body(Collections.singletonMap("error","중복된 이메일이 존재합니다"));

        return ResponseEntity.ok().body(Collections.singletonMap("message","중복된 이메일이 없습니다"));
    }
}
