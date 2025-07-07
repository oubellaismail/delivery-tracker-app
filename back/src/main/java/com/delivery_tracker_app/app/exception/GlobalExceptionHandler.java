package com.delivery_tracker_app.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Create a HashMap to store field errors
        Map<String, String> errors = new HashMap<>();

        // Iterate over all field errors from the exception's BindingResult
        ex.getBindingResult().getAllErrors().forEach(error -> {
            // Check if the error is a FieldError (most common for @Valid)
            // It's good practice to ensure it's a FieldError before casting
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {
                // Handle non-field errors (e.g., object-level constraints) if necessary
                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });

        // Return a ResponseEntity with the errors map and HTTP 400 Bad Request status
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
