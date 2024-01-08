package org.example.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.common.exception.member.EmailAlreadyExistingException;
import org.example.common.exception.member.JwtAccessTokenAlreadyLogoutException;
import org.example.common.exception.member.PasswordNotMatchingException;
import org.example.config.JwtTokenProvider;
import org.example.domain.logout.Logout;
import org.example.domain.logout.LogoutRepository;
import org.example.domain.member.Member;
import org.example.domain.member.MemberRepository;
import org.example.dto.auth.LoginRequestDto;
import org.example.dto.auth.LogoutRequestDto;
import org.example.dto.auth.TokenInfo;
import org.example.dto.register.RegisterRequestDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final LogoutRepository logoutRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final Random random;
    public TokenInfo login(LoginRequestDto loginRequestDto) throws PasswordNotMatchingException {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),loginRequestDto.getPassword());
        Authentication authentication;
        try{
            authentication = authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
        }catch(Exception e){
            throw new PasswordNotMatchingException();
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

    //로그아웃 시 Logout Table에 Access Token 저장
    @Transactional
    public void logout(HttpServletRequest httpServletRequest){

        String accessToken = jwtTokenProvider.getAccessTokenWithValid(httpServletRequest);
        logoutRepository.save(LogoutRequestDto.builder()
                        .token(accessToken)
                        .expiration(LocalDateTime.now().plus(Duration.ofMinutes(30)))
                        .build().toEntity());
    }

    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        if(emailExist(registerRequestDto.getEmail())) throw new EmailAlreadyExistingException("이메일이 이미 존재합니다");
        registerRequestDto.setPassword(bCryptPasswordEncoder.encode(registerRequestDto.getPassword()));
        memberRepository.save(registerRequestDto.toEntity());
    }



    @Transactional
    public boolean emailExist(String email) {
        Optional<Member> existingUser = memberRepository.findByEmail(email);
        return existingUser.isPresent();
    }

    public Cookie getRefreshTokenCookie(HttpServletRequest httpServletRequest){
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public int createRandomCodeForEmailAuthentication(){
        return random.nextInt(900000) + 100000;
    }

    public void sendEmailWithCode(int code){
        //TODO : 이메일 전송 로직
    }

}
