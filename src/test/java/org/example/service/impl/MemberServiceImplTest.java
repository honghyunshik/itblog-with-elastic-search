package org.example.service.impl;

import org.example.domain.member.Member;
import org.example.domain.member.MemberRepository;
import org.example.dto.register.EmailRequestDto;
import org.example.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;

    @Test
    public void 중복된_이메일이_있는경우_true_반환한다(){
        EmailRequestDto emailRequestDto = new EmailRequestDto("admin@naver.com");
        when(memberRepository.findByEmail(emailRequestDto.getEmail())).thenReturn(Optional.of(new Member()));

        assertTrue(memberService.emailExist(emailRequestDto));
    }

    @Test
    public void 중복된_이메일이_없는경우_false_반환한다(){
        EmailRequestDto emailRequestDto = new EmailRequestDto("admin@naver.com");
        when(memberRepository.findByEmail(emailRequestDto.getEmail())).thenReturn(Optional.empty());

        assertFalse(memberService.emailExist(emailRequestDto));
    }
}