package org.example.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.constants.JwtConstants;
import org.example.common.exception.member.EmailAlreadyExistingException;
import org.example.common.exception.member.ValidationException;
import org.example.dto.error.ErrorResponseDto;
import org.example.dto.auth.LoginRequestDto;
import org.example.dto.auth.TokenInfo;
import org.example.dto.register.EmailRequestDto;
import org.example.dto.register.EmailResponseDto;
import org.example.dto.register.RegisterRequestDto;
import org.example.dto.success.SuceessResponseDto;
import org.example.service.impl.MemberServiceImpl;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Tag(name = "member auth", description = "유저 인증/인가 API")
@RequestMapping(value = "/api/v1/member")
@RestController
@RequiredArgsConstructor
public class MemberAuthApiController {

    private final MemberServiceImpl memberService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공, Response Header에 Access Token, Refresh Token 반환", content = @Content(schema = @Schema(implementation = SuceessResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "id/pw 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "id/pw 오류로 로그인 실패 or 이미 로그인 되어 있는 경우", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "유저 로그인 API", description = "id/pw를 통해 인증합니다")
    @PostMapping("/login")
    public ResponseEntity<SuceessResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto, Errors errors){
        //TODO : 이미 로그인이 되어 있는 경우 handle
        if(errors.hasErrors()) throw new ValidationException();

        TokenInfo tokenInfo = memberService.login(loginRequestDto);
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenInfo.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(JwtConstants.HEADER,tokenInfo.getAccessToken())
                .header("Set-Cookie",refreshTokenCookie.toString())
                .body(new SuceessResponseDto("로그인에 성공했습니다"));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = SuceessResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인 되어있지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "로그아웃 API", description = "로그아웃합니다")
    @PostMapping("/logout")
    public ResponseEntity<SuceessResponseDto> logout(HttpServletRequest httpServletRequest){

        memberService.logout(httpServletRequest);
        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken",null)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", expiredCookie.toString())
                .body(new SuceessResponseDto("로그아웃 했습니다"));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = SuceessResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "email/pw/birth/name/gender 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "이메일이 이미 존재함", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "유저 회원가입 API", description = "email/pw/birth/name/gender가 필요한 회원가입 API")
    @PostMapping("/register")
    public ResponseEntity<SuceessResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto, Errors errors){

        if(errors.hasErrors()) {
            System.out.println(errors);
            throw new ValidationException();
        };

        memberService.register(registerRequestDto);

        return ResponseEntity.ok()
                .body(new SuceessResponseDto("회원가입이 완료되었습니다"));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 발송 성공", content = @Content(schema = @Schema(implementation = SuceessResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "email 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "이메일이 이미 존재함", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "이메일 인증 코드 API", description = "이메일로 인증 코드를 전송하고 6자리 랜덤 코드를 반환합니다")
    @PostMapping("/email")
    public ResponseEntity<EmailResponseDto> emailCheck(@Valid @RequestBody EmailRequestDto emailRequestDto, Errors errors){

        //이메일 유효성 검사 실패시 bad request + error 내용 반환
        if (errors.hasErrors()) throw new ValidationException();

        //이메일이 이미 존재할 경우 401 반환
        if(memberService.emailExist(emailRequestDto.getEmail())) throw new EmailAlreadyExistingException("이메일이 이미 존재합니다");

        int code = memberService.createRandomCodeForEmailAuthentication();
        memberService.sendEmailWithCode(code);

        return ResponseEntity.ok().body(new EmailResponseDto(code));
    }
}
