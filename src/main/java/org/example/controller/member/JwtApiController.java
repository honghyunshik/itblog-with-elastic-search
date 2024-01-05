package org.example.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.common.constants.JwtConstants;
import org.example.common.exception.member.JwtRefreshTokenNotValidException;
import org.example.config.JwtTokenProvider;
import org.example.dto.error.ErrorResponseDto;
import org.example.dto.success.SuceessResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "member jwt", description = "jwt token API")
@RequestMapping(("/api/v1/member"))
@RestController
@RequiredArgsConstructor
public class JwtApiController {

    private final JwtTokenProvider jwtTokenProvider;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신에 성공함", content = @Content(schema = @Schema(implementation = SuceessResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "리프레쉬 토큰이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "token refresh API", description = "refresh token을 통해 access token을 갱신하는 API")
    @PostMapping("/refresh")
    public ResponseEntity<SuceessResponseDto> refreshAccessToken(HttpServletRequest httpServletRequest){

        String refreshToken = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        try{
            jwtTokenProvider.isValidateToken(refreshToken);
        } catch (Exception e) {
            throw new JwtRefreshTokenNotValidException();
        }

        String refreshedAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);
        return ResponseEntity.ok()
                .header(JwtConstants.HEADER,refreshedAccessToken)
                .body(new SuceessResponseDto("토큰을 갱신했습니다"));
    }
}
