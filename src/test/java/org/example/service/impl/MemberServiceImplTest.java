package org.example.service.impl;

import org.example.common.exception.member.EmailAlreadyExistingException;
import org.example.domain.member.Member;
import org.example.domain.member.MemberRepository;
import org.example.dto.register.RegisterRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Nested
    @DisplayName("회원가입 테스트")
    class RegisterTest{
        @Test
        public void 중복된_이메일이_있는경우_예외를_던진다(){
            EmailRequestDto emailRequestDto = new EmailRequestDto("admin@naver.com");
            when(memberRepository.findByEmail(emailRequestDto.getEmail())).thenReturn(Optional.of(new Member()));

            assertThrows(EmailAlreadyExistingException.class,()->memberService.emailExist(emailRequestDto));
        }

        @Test
        public void 중복된_이메일이_없는경우_예외를_던지지_않는다(){
            EmailRequestDto emailRequestDto = new EmailRequestDto("admin@naver.com");
            when(memberRepository.findByEmail(emailRequestDto.getEmail())).thenReturn(Optional.empty());

            assertDoesNotThrow(()->memberService.emailExist(emailRequestDto));
        }

        @Test
        public void BCrypt로_비밀번호를_암호화한다(){
            RegisterRequestDto registerRequestDto = new RegisterRequestDto();
            registerRequestDto.setPassword("password");
            when(bCryptPasswordEncoder.encode(registerRequestDto.getPassword())).thenReturn("encrypt");
            memberService.register(registerRequestDto);
            assertEquals(registerRequestDto.getPassword(),"encrypt");
        }
    }


}