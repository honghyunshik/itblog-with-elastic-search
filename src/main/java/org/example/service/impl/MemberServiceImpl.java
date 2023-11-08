package org.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.domain.member.Member;
import org.example.domain.member.MemberRepository;
import org.example.dto.register.EmailRequestDto;
import org.example.dto.register.RegisterRequestDto;
import org.example.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        /*
        //이메일이나
        memberRepository.findByEmailOrNickname(registerRequestDto.getEmail(), registerRequestDto.getNickname())
                .ifPresent(user -> {
                    throw new UserExistException();
                });

        memberRegRequestDto.setPassword(bCryptPasswordEncoder.encode(memberRegRequestDto.getPassword()));
        HashMap<String,Long> map = new HashMap<>();
        map.put("id",memberRepository.save(memberRegRequestDto.toEntity()).getId());

        return new ResponseEntity<>(map, HttpStatus.OK);
        */

    }

    @Override
    @Transactional
    public boolean emailExist(EmailRequestDto emailRequestDto) {
        Optional<Member> existingUser = memberRepository.findByEmail(emailRequestDto.getEmail());

        //이메일에 중복이 있는지 체크
        return existingUser.isPresent();
    }
}
