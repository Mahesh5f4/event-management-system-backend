package com.eventhub.backend.common.exception;

import com.eventhub.backend.common.response.ApiResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                new ApiResponse<>(false, null, ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequest(BadRequestException ex) {
        return new ResponseEntity<>(
                new ApiResponse<>(false, null, ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {
        ex.printStackTrace();   // changed
        return new ResponseEntity<>(
                new ApiResponse<>(false, null, ex.getMessage()),   // changed
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}