package org.example.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.common.constants.WhiteList;
import org.example.config.JwtTokenProvider;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final String[] WHITE_LIST = WhiteList.WHITE_LIST;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        //White List는 filter 통과
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for(String whiteList:WHITE_LIST){
            if(pathMatcher.match(whiteList,request.getRequestURI())){
                filterChain.doFilter(request,response);
                return;
            }
        }

        if(accessToken!=null) {

            try{
                jwtTokenProvider.isValidateToken(accessToken);
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                //Access Token이 만료되었다면 Refresh Token으로 새로운 Access Token 발급받아야 함
                unauthorized(response, "토큰이 만료되었습니다");
                return;
            }catch (SecurityException | MalformedJwtException e){
                unauthorized(response,"토큰이 변조되었습니다");
                return;
            }catch(UnsupportedJwtException e){
                unauthorized(response,"지원하지 않는 타입의 토큰입니다");
                return;
            }catch (IllegalArgumentException e){
                unauthorized(response,"토큰의 claim이 비었습니다");
                return;
            }catch(Exception e){
                unauthorized(response,e.getMessage());

                return;
            }
        }else{
            unauthorized(response,"토큰이 존재하지 않습니다");
            return;
        }
        filterChain.doFilter(request,response);
    }

    private void unauthorized(HttpServletResponse httpServletResponse, String message) throws IOException {

        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error",message)));
    }
}
