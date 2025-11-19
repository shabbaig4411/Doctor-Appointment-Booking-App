package com.auth_service.exception;

import com.auth_service.dto.ApiResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse<?>> invalidPasswordExceptionHandler(InvalidPasswordException e, WebRequest request) {
        ApiResponse res = new ApiResponse();
        res.setMessage(e.getMessage());
        res.setCode(400);
        res.setData("Invalid Password!!!  Try With a valid Password!!!!");
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> anyExceptionNotHandled(Exception e, WebRequest request) {
        ApiResponse res = new ApiResponse();
        res.setMessage(e.getMessage());
        res.setCode(500);
        res.setData("Something went wrong!!!");
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }
}
