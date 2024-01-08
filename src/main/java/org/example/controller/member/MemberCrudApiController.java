package org.example.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.error.ErrorResponseDto;
import org.example.dto.profile.PutProfileRequestDto;
import org.example.dto.profile.SelectProfileResponseDto;
import org.example.dto.success.SuceessResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "member crud", description = "유저 CRUD API")
@RequestMapping(value = "/api/v1/member")
@RestController
@RequiredArgsConstructor
public class MemberCrudApiController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원탈퇴 성공", content = @Content(schema = @Schema(implementation = SuceessResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "토큰이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "유저 삭제 API", description = "유저를 삭제합니다")
    @DeleteMapping("/profile")
    public ResponseEntity<SuceessResponseDto> deleteUser(HttpServletRequest httpServletRequest){
        //TODO : 회원 정보 삭제 + 로그아웃 + 쿠키 삭제

        return ResponseEntity.ok()
                .body(new SuceessResponseDto("회원 탈퇴에 성공했습니다"));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 수정 성공", content = @Content(schema = @Schema(implementation = SuceessResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "토큰이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "유저 수정 API", description = "유저를 수정합니다")
    @PutMapping("/profile")
    public ResponseEntity<SuceessResponseDto> putUser(HttpServletRequest httpServletRequest,
                                                      @Valid @RequestBody PutProfileRequestDto putProfileRequestDto){
        //TODO : 회원 정보 수정

        return ResponseEntity.ok()
                .body(new SuceessResponseDto("회원 수정에 성공했습니다"));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 조회 성공", content = @Content(schema = @Schema(implementation = SuceessResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "토큰이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "유저 조회 API", description = "유저를 조회합니다")
    @GetMapping("/profile")
    public ResponseEntity<SelectProfileResponseDto> selectUser(HttpServletRequest httpServletRequest){
        //TODO : 회원 정보 조회

        return ResponseEntity.ok()
                .body(new SelectProfileResponseDto());
    }
}
