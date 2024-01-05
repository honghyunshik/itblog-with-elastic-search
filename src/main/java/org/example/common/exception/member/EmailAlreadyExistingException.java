package org.example.common.exception.member;

public class EmailAlreadyExistingException extends RuntimeException{

    public EmailAlreadyExistingException(String msg){
        super(msg);
    }
}
