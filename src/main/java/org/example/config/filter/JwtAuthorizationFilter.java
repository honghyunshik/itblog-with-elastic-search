package org.example.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.common.constants.JwtConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken((HttpServletRequest) request);
    }

    //Request Header 에서 Token 정보를 추출합니다
    private String resolveToken(HttpServletRequest httpServletRequest){
        String authHeader = httpServletRequest.getHeader(JwtConstants.HEADER);
        if(authHeader!=null && authHeader.startsWith(JwtConstants.TYPE)) {
            //Bearer을 제외한 Token 값을 추출합니다
            return authHeader.substring(7);
        }
        return null;
    }
}
