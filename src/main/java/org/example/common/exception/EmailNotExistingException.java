package org.example.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class EmailNotExistingException extends RuntimeException{

    public EmailNotExistingException(String msg){
        super(msg);
    }
}
