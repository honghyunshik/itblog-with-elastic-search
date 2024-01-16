package org.example.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.common.exception.member.JwtAccessTokenAlreadyLogoutException;
import org.example.domain.logout.Logout;
import org.example.domain.logout.LogoutRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl {

    private final LogoutRepository logoutRepository;

    @Transactional
    public void isLogout(String accessToken){
        Optional<Logout> logout = logoutRepository.findById(accessToken);
        if(logout.isPresent()){
            throw new JwtAccessTokenAlreadyLogoutException();
        }
    }

}
