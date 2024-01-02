package org.example.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.common.constants.JwtConstants;
import org.example.dto.login.TokenInfo;
import org.example.service.impl.MemberLoginServiceimpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    //private RedisLoginServiceImpl redisLoginService;

    private MemberLoginServiceimpl memberLoginServiceimpl
    private Key key;

    //기간 30분
    private static int ACCESS_EXPIRE_TIME = 30*60*1000;
    //기간 1주일
    private static int REFRESH_EXPIRE_TIME = 7*24*60*60*1000;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, MemberLoginServiceimpl memberLoginServiceimpl) {

        this.memberLoginServiceimpl = memberLoginServiceimpl;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //Access Token이 만료됐지만 refresh Token이 Redis에 존재할 경우 새로운 access Token 발급
    //발급하는 기간은 30분과 refresh Token의 min값
    public TokenInfo refreshAccessToken(Authentication authentication){

        LoginRedisResponseDto loginRedisResponseDto = redisLoginService.tokenExist(authentication.getName());
        if(loginRedisResponseDto==null) return null;
        String refreshToken = loginRedisResponseDto.getToken();
        int refreshTTL = loginRedisResponseDto.getExpiration();
        String accessToken = generateAccessToken(authentication,generateAuthorities(authentication),
                Math.min(ACCESS_EXPIRE_TIME,refreshTTL));
        return TokenInfo.builder()
                .grantType(JwtConstants.TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //토큰의 만료기간 얻기
    public int getExpirationTime(String token){
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
        long now = new Date().getTime();
        return (int) (expiration.getTime()-now);
    }

    public String generateAuthorities(Authentication authentication){

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    //accessToken의 유지기간은 30분
    //refreshToken이 30분 이하로 남았을 경우 refresh Token이 만료된 이후 로그인 필요
    public String generateAccessToken(Authentication authentication, String authorities, int time){

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
                .setExpiration(new Date(now + REFRESH_EXPIRE_TIME))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenInfo generateToken(Authentication authentication){

        String authorities = generateAuthorities(authentication);

        //accessToken 유지기간은 30분
        String accessToken = generateAccessToken(authentication,authorities,ACCESS_EXPIRE_TIME);

        //refreshToken 유지기간은 2주
        String refreshToken = generateRefreshToken(authentication,authorities);

        //refresh token redis 저장
        redisLoginService.save(LoginRedisRequestDto.builder()
                        .email(authentication.getName())
                        .token(refreshToken)
                        .expiration(REFRESH_EXPIRE_TIME)
                        .build());

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

    public Authentication getAuthentication(String accessToken){

        Claims claims = parseClaims(accessToken);

        if(claims.get("auth")==null) throw new RuntimeException("권한 정보가 없는 토큰입니다");

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(),"",authorities);

        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }
}