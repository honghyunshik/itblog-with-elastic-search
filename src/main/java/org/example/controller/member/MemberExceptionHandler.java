package org.example.controller.member;

import org.example.common.exception.member.*;
import org.example.dto.error.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler(PasswordNotMatchingException.class)
    public ResponseEntity<ErrorResponseDto> passwordNotMatchingExceptionHandler(){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto("비밀번호가 틀렸습니다"));
    }

    @ExceptionHandler(JwtAccessTokenExpiredException.class)
    public ResponseEntity<ErrorResponseDto> jwtAccessTokenExpiredExceptionHandler(){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto("AccessToken이 만료되었습니다"));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> validationException(){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("유효성 검사에 실패했습니다"));
    }

    @ExceptionHandler(JwtRefreshTokenNotValidException.class)
    public ResponseEntity<ErrorResponseDto> jwtRefreshTokenNotValidExceptionHandler(){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto("유효하지 않은 Refresh Token입니다"));
    }

    @ExceptionHandler(EmailAlreadyExistingException.class)
    public ResponseEntity<ErrorResponseDto> emailAlreadyExistingExceptionHandler(){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto("이메일이 이미 존재합니다"));
    }
}