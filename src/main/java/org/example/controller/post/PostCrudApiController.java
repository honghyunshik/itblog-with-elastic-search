package org.example.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.exception.member.EmailAlreadyExistingException;
import org.example.common.exception.member.ValidationException;
import org.example.dto.error.ErrorResponseDto;
import org.example.dto.post.PostpostRequestDto;
import org.example.dto.register.EmailResponseDto;
import org.example.dto.success.SuceessResponseDto;
import org.example.service.impl.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "member auth", description = "유저 인증/인가 API")
@RestController
@RequestMapping("/api/v1/post")
public class PostCrudApiController {

    private final PostServiceImpl postService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 작성 성공", content = @Content(schema = @Schema(implementation = SuceessResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "게시글 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "게시글 작성권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "포스트 작성 API", description = "제목과 내용으로 블로그를 작성합니다")
    @PostMapping("/posts")
    public ResponseEntity<SuceessResponseDto> writePost(@Valid @RequestBody PostpostRequestDto postpostRequestDto,
                                                        HttpServletRequest httpServletRequest, Errors errors){

        //이메일 유효성 검사 실패시 bad request + error 내용 반환
        if (errors.hasErrors()) throw new ValidationException();

        postService.post(postpostRequestDto,httpServletRequest);

        return ResponseEntity.ok().body(new SuceessResponseDto("게시글을 작성했습니다"));
    }
}
