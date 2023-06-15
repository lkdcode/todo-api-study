package com.example.todo.userapi.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class UploadFileExceptionHandler {
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> uploadFileExceptionHandler(IOException e) {
        log.error("źzzzzzzzzzzßjgdaacvb");
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
