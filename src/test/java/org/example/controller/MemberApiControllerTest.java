package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.JwtTokenProvider;
import org.example.dto.register.EmailRequestDto;
import org.example.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MemberApiController.class)
class MemberApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void 유효성_검사를_실패하면_bad_request_반환한다() throws Exception {
        EmailRequestDto emailRequestDto = new EmailRequestDto("admin@");

        this.mvc.perform(post("/api/v1/member/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 유효성_검사를_성공하고_중복된_이메일이면_unauthorized_반환한다() throws Exception{
        EmailRequestDto duplicatedEmailRequestDto = new EmailRequestDto("admin@naver.com");

        this.mvc.perform(post("/api/v1/member/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatedEmailRequestDto)))
                .andExpect(status().isUnauthorized());
     }

    @Test
    public void 유효성_검사를_성공하고_중복된_이메일이_아니면_200_반환한다() throws Exception{
        EmailRequestDto successEmailRequestDto = new EmailRequestDto("admin@naver.com");

        this.mvc.perform(post("/api/v1/member/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successEmailRequestDto)))
                .andExpect(status().isOk());
    }
}