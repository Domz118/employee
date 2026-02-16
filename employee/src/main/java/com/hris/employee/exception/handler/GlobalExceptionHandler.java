package com.hris.employee.exception.handler;

import com.hris.employee.dto.response.ErrorResponse;
import com.hris.employee.utils.XssSanitizer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final XssSanitizer sanitizer;

    public GlobalExceptionHandler(XssSanitizer sanitizer) {
        this.sanitizer = sanitizer;
    }
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?>handValidJson(HttpMessageNotReadableException ex){
       return ResponseEntity.badRequest().body(Map.of("status",400,"error","Malformed JSON request"));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(XSSServletException.class)
    public ErrorResponse handleSQLException(HttpServletRequest request,
                                            Exception ex){
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setMessage(ex.getMessage());

        return errorResponse;
    }



}
