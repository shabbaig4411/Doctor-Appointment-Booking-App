package com.auth_service.dto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiResponse<T> {

    private String message;
    private int  code;
    private T data;

}
