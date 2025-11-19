package com.auth_service.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String msg){
        super(msg);
    }
}
