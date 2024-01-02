package org.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.common.exception.EmailNotExistingException;
import org.example.common.exception.PasswordNotMatchingException;
import org.example.config.JwtTokenProvider;
import org.example.domain.member.Member;
import org.example.domain.member.MemberRepository;
import org.example.dto.login.LoginRequestDto;
import org.example.dto.login.TokenInfo;
import org.example.dto.register.EmailRequestDto;
import org.example.dto.register.RegisterRequestDto;
import org.example.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public TokenInfo login(LoginRequestDto loginRequestDto) throws PasswordNotMatchingException {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),loginRequestDto.getPassword());
        Authentication authentication;
        try{
            authentication = authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
        }catch(Exception e){
            throw new PasswordNotMatchingException("비밀번호가 틀렸습니다");
        }
        //login 시 토큰 발급 -> 로그인 기준으로 access Token : 30분   refresh Token : 1주
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return memberRepository.findByEmail(username)
                .map(member->{
                    return User.builder()
                            .username(member.getEmail())
                            .password(member.getPassword())
                            .roles(String.valueOf(member.getRole()))
                            .build();
                }).orElseThrow(()->new UsernameNotFoundException("해당 이메일의 유저를 찾을 수 없습니다"));
    }

    @Override
    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        emailExist(registerRequestDto);
        registerRequestDto.setPassword(bCryptPasswordEncoder.encode(registerRequestDto.getPassword()));
        memberRepository.save(registerRequestDto.toEntity());
    }

    @Override
    @Transactional
    public void emailExist(EmailRequestDto emailRequestDto) {
        Optional<Member> existingUser = memberRepository.findByEmail(emailRequestDto.getEmail());
        if(existingUser.isPresent()) throw new EmailNotExistingException("이메일이 이미 존재합니다");
    }
}
