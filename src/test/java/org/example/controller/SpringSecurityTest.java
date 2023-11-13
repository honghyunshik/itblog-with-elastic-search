package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.example.common.constants.WhiteList;
import org.example.config.JwtTokenProvider;
import org.example.domain.member.Member;
import org.example.domain.member.MemberRepository;
import org.example.domain.member.Role;
import org.example.dto.login.LoginRequestDto;
import org.example.dto.login.TokenInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeAll
    public void init() throws Exception {

        when(memberRepository.findByEmail("rightEmail@naver.com")).thenReturn(Optional.ofNullable(Member.builder()
                .email("rightEmail@naver.com")
                .password("rightPassword")
                .role(Role.USER)
                .build()));

        when(memberRepository.findByEmail("wrongEmail@naver.com")).thenReturn(Optional.empty());
    }

    @Test
    public void 로그인시_이메일을_틀리면_401_반환한다() throws Exception {
        LoginRequestDto wrongEmailLoginRequestDto = new LoginRequestDto("wrongEmail@naver.com","password");
        postLoginWithUnAuthorized(wrongEmailLoginRequestDto);
    }

    @Test
    public void 로그인시_비밀번호를_틀리면_401_반환한다() throws Exception{
        LoginRequestDto wrongPasswordLoginRequestDto = new LoginRequestDto("wrongEmail@naver.com","password");
        postLoginWithUnAuthorized(wrongPasswordLoginRequestDto);
    }

    @Test
    public void 유효한_토큰이_로그아웃_기록이_없으면_200_반환한다() throws Exception {
        doNothing().when(jwtTokenProvider).isValidateToken("token");

        mockMvc.perform(get("/index")
                .header("Authorization","Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    public void 유효한_토큰이_로그아웃한_기록이_있으면_401_반환한다() throws Exception{
        doNothing().when(jwtTokenProvider).isValidateToken("token");

        RedisTemplate<String,Object> redisTemplate = mock(RedisTemplate.class);
        ValueOperations<String,Object> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("token")).thenReturn("logout");

        mockMvc.perform(get("/index")
                        .header("Authorization","Bearer token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void 만료된_토큰인뎨_refresh_token_이_있다() throws Exception{
        doThrow(ExpiredJwtException.class).when(jwtTokenProvider).isValidateToken("token");

        User principal = new User("username", "password", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(jwtTokenProvider.getAuthentication("token")).thenReturn(authentication);
        TokenInfo newToken = new TokenInfo();
        newToken.setAccessToken("newToken");
        when(jwtTokenProvider.refreshAccessToken(authentication)).thenReturn(newToken);

        mockMvc.perform(get("/index")
                        .header("Authorization","Bearer token"))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization","newToken"));
    }

    @Test
    public void 만료된_토큰인데_refresh_token_이_없다() throws Exception{
        doThrow(ExpiredJwtException.class).when(jwtTokenProvider).isValidateToken("token");

        User principal = new User("username", "password", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(jwtTokenProvider.getAuthentication("token")).thenReturn(authentication);
        when(jwtTokenProvider.refreshAccessToken(authentication)).thenReturn(null);

        mockMvc.perform(get("/index")
                        .header("Authorization","Bearer token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("로그인한지 너무 오래됐습니다"));
    }
    @Test
    public void 변조된_토큰으로_요청하면_401_반환한다() throws Exception{
        doThrow(MalformedJwtException.class).when(jwtTokenProvider).isValidateToken("token");

        mockMvc.perform(get("/index")
                        .header("Authorization","Bearer token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("토큰이 변조되었습니다"));
    }

    @Test
    public void WHITE_LIST_아닌_URL_로_토큰_없이_요청한다() throws Exception{
        mockMvc.perform(get("/index"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("토큰이 존재하지 않습니다"));
    }

    @Test
    public void WHITE_LIST_인_URL_로_토큰_없이_요청한다() throws Exception{
        String white_list_url = WhiteList.WHITE_LIST[0];
        mockMvc.perform(get(white_list_url))
                .andExpect(status().isNotFound());
    }

    private void postLoginWithUnAuthorized(LoginRequestDto loginRequestDto) throws Exception {
        mockMvc.perform((post("/api/v1/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))))
                .andExpect(status().isUnauthorized());
    }

}
