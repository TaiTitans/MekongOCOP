package com.mekongocop.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBindException(BindException e) {
        String errorMessage = "Request not accept";
        if (e.getBindingResult().hasErrors()) {
            errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }
        return errorMessage;
    }
}
