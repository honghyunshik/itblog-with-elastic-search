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
import org.example.common.exception.member.ValidationException;
import org.example.dto.error.ErrorResponseDto;
import org.example.dto.post.SavepostRequestDto;
import org.example.dto.post.SavePostResponseDto;
import org.example.dto.post.SelectPostResponseDto;
import org.example.service.impl.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "member auth", description = "유저 인증/인가 API")
@RestController
@RequestMapping("/api/v1/post")
public class PostCrudApiController {

    private final PostServiceImpl postService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 작성 성공", content = @Content(schema = @Schema(implementation = SavePostResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "게시글 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "게시글 작성권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "포스트 작성 API", description = "제목과 내용으로 블로그를 작성합니다")
    @PostMapping("/posts")
    public ResponseEntity<SavePostResponseDto> writePost(@Valid @RequestBody SavepostRequestDto savepostRequestDto,
                                                         HttpServletRequest httpServletRequest, Errors errors){

        //이메일 유효성 검사 실패시 bad request + error 내용 반환
        if (errors.hasErrors()) throw new ValidationException();

        long id = postService.savePost(savepostRequestDto,httpServletRequest);

        return ResponseEntity.ok().body(new SavePostResponseDto(id));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content(schema = @Schema(implementation = SelectPostResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "게시글 조회 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 문제", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Operation(summary = "포스트 조회 API", description = "postNum에 맞는 포스트를 조회합니다")
    @GetMapping("/posts/{id}")
    public ResponseEntity<SelectPostResponseDto> writePost(@PathVariable("id") long id){

        SelectPostResponseDto selectPostResponseDto = postService.selectPost(id);

        return ResponseEntity.ok().body(selectPostResponseDto);
    }
}
