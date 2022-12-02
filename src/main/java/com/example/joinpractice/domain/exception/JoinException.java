package com.example.joinpractice.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinException extends RuntimeException {
    private ErrorCode errorCode;
    private String message;


}
