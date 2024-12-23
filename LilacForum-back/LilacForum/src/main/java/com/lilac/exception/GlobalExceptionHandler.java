package com.lilac.exception;

import com.lilac.pojo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Result> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(Result.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
