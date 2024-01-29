package org.example.controller.post;

import org.example.common.exception.member.PasswordNotMatchingException;
import org.example.common.exception.post.PostNotExistException;
import org.example.dto.error.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PostExceptionHandler {

    @ExceptionHandler(PostNotExistException.class)
    public ResponseEntity<ErrorResponseDto> postNotExistExceptionHandler(){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("게시물이 존재하지 않습니다"));
    }
}
