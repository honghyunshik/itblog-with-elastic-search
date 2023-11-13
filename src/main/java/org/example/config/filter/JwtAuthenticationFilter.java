package org.example.config.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.common.constants.JwtConstants;
import org.example.common.constants.WhiteList;
import org.example.config.JwtTokenProvider;
import org.example.dto.login.TokenInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

    @Component
    @RequiredArgsConstructor
    public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenProvider jwtTokenProvider;
        private final RedisTemplate<String,Object> redisTemplate;
        private final String[] WHITE_LIST = WhiteList.WHITE_LIST;
        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain) throws ServletException, IOException {

            String accessToken = resolveToken((HttpServletRequest) request);

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
                    //로그아웃 되어있지 않다면 filter 통과
                    String logout = (String) redisTemplate.opsForValue().get(accessToken);
                    if (ObjectUtils.isEmpty(logout)) {
                        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                    //로그아웃 되어있다면 filter 통과 못함 => 다시 로그인
                    else {
                        unauthorized(response, "로그아웃 되었습니다");
                        return;
                    }
                } catch (ExpiredJwtException e) {

                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    TokenInfo tokenInfo = jwtTokenProvider.refreshAccessToken(authentication);

                    //토큰이 단순히 만료된거라면 새로운 access token 발급
                    if (tokenInfo != null) {
                        response.addHeader(JwtConstants.HEADER, tokenInfo.getAccessToken());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                    //refresh token도 만료됐다면 filter 통과 못함 => 다시 로그인
                    else {
                        unauthorized(response, "로그인한지 너무 오래됐습니다");
                        return;
                    }
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
                    unauthorized(response,"유효하지 않은 토큰입니다");
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
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.getWriter().write(message);
            httpServletResponse.getWriter().flush();
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
