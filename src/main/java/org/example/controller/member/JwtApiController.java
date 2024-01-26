package org.example.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.common.constants.JwtConstants;
import org.example.common.exception.member.JwtTokenNotValidException;
import org.example.config.JwtTokenProvider;
import org.example.dto.error.ErrorResponseDto;
import org.example.dto.auth.LoginStatusResponseDto;
import org.example.dto.success.SuceessResponseDto;
import org.example.service.impl.MemberServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

@Tag(name = "member jwt", description = "jwt token API")
@RequestMapping(("/api/v1/member"))
@RestController
@RequiredArgsConstructor
public class JwtApiController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberServiceImpl memberService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신에 성공함", content = @Content(schema = @Schema(implementation = LoginStatusResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "리프레쉬 토큰이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "token refresh API", description = "refresh token을 통해 access token을 갱신하는 API")
    @GetMapping("/refresh")
    public ResponseEntity<LoginStatusResponseDto> refreshAccessToken(HttpServletRequest httpServletRequest){

        String refreshToken = memberService.getRefreshTokenCookie(httpServletRequest).getValue();
        try{
            jwtTokenProvider.isValidateToken(refreshToken);
        } catch (Exception e) {
            throw new JwtTokenNotValidException();
        }

        String refreshedAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);
        return ResponseEntity.ok()
                .header(JwtConstants.HEADER,refreshedAccessToken)
                .body(new LoginStatusResponseDto(true, jwtTokenProvider.getAuthentication(refreshToken).getName()));
    }

   @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 상태 성공적으로 반환함", content = @Content(schema = @Schema(implementation = LoginStatusResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "로그인 상태 확인 API", description = "Access Token을 검증하여 로그인 상태를 확인합니다")
    @GetMapping("/login-status")
    public ResponseEntity<LoginStatusResponseDto> login_status(HttpServletRequest httpServletRequest){

       String accessToken = jwtTokenProvider.getAccessTokenWithValid(httpServletRequest);

        if(accessToken==null) return ResponseEntity.ok()
                    .body(new LoginStatusResponseDto(false,null));

        return ResponseEntity.ok()
                .body(new LoginStatusResponseDto(true, jwtTokenProvider.getAuthentication(accessToken).getName()));
    }

}
