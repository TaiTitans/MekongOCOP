package com.mekongocop.mekongocopserver.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String error;
    private String message;

}
