package com.hris.employee.exception.handler;

import lombok.Getter;

import java.util.List;

@Getter
public class FieldValidationException extends RuntimeException{

    private final List<String> errors;

    public FieldValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}
