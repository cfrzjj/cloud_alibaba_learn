package com.liu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

// 摘至 spring cloud alibaba console 模块处理
public class ConsoleExceptionHandler {

    @ExceptionHandler(Exception.class)
    private ResponseEntity<String> handleAccessException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}