package org.example.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.common.constants.JwtConstants;
import org.example.common.constants.JwtExpiration;
import org.example.dto.login.TokenInfo;
import org.example.service.impl.MemberLoginServiceimpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //Access Token이 만료됐지만 refresh Token이 유효할 경우 새로운 access Token 발급
    //발급하는 기간은 30분과 refresh Token의 min값
    public String refreshAccessToken(String refreshToken){

        Authentication authentication = getAuthentication(refreshToken);

        return generateAccessToken(authentication,
        generateAuthorities(authentication), JwtExpiration.ACCESS_TOKEN_EXPIRATION);
    }

    public String generateAuthorities(Authentication authentication){

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    //accessToken의 유지기간은 30분
    //refreshToken이 30분 이하로 남았을 경우 refresh Token이 만료된 이후 로그인 필요
    public String generateAccessToken(Authentication authentication, String authorities, long time){

        long now = new Date().getTime();

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth",authorities)
                .setExpiration(new Date(now + time))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //refreshToken의 유지기간은 2주
    public String generateRefreshToken(Authentication authentication, String authorities){

        long now = new Date().getTime();

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth",authorities)
                .setExpiration(new Date(now + JwtExpiration.REFRESH_TOKEN_EXPIRATION))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenInfo generateToken(Authentication authentication){

        String authorities = generateAuthorities(authentication);

        //accessToken 유지기간은 30분
        String accessToken = generateAccessToken(authentication,authorities,JwtExpiration.ACCESS_TOKEN_EXPIRATION);

        //refreshToken 유지기간은 2주
        String refreshToken = generateRefreshToken(authentication,authorities);

        return TokenInfo.builder()
                .grantType(JwtConstants.TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private Claims parseClaims(String accessToken){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().
                    parseClaimsJws(accessToken).getBody();
        }catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }

    //예외를 상위 메서드로 Throw
    public void isValidateToken(String token) throws Exception{
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public Authentication getAuthentication(String token){

        Claims claims = parseClaims(token);

        if(claims.get("auth")==null) throw new RuntimeException("권한 정보가 없는 토큰입니다");

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(),"",authorities);

        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }
}