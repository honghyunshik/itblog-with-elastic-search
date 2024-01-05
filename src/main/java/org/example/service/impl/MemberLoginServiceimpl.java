package org.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.domain.login.Login;
import org.example.domain.login.LoginRepository;
import org.example.dto.login.LoginSaveRequestDto;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.DateUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberLoginServiceimpl {

    private final LoginRepository loginRepository;

    //로그인 시 로그인 한 이메일, 만료기한을 DB에 저장
    @Transactional
    public void saveLoginDetails(LoginSaveRequestDto loginSaveRequestDto){
        loginRepository.save(loginSaveRequestDto.toEntity());
    }

    //만료된 토큰 연장 여부 결정
    @Transactional
    public Integer isRefreshTokenExpired(String token){
        Optional<Login> login = loginRepository.findByEmailNotExpired(token);
        return login.map(value -> getExpirationWithMilleSeconds(value.getExpiration())).orElse(null);
    }

    //남은 시간 밀리세컨즈 단위로 변환
    private Integer getExpirationWithMilleSeconds(LocalDate localDate){
        Long targetMillis = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long currentMillis = System.currentTimeMillis();
        return Math.toIntExact(targetMillis - currentMillis);
    }
}