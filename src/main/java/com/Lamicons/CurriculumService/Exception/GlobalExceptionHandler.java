package com.Lamicons.CurriculumService.Exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ApiResponse(responseCode = "404", description = "Resource not found", 
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        return buildErrorResponse(ex, "Resource not found", HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = ValidationErrorResponse.class)))
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errorMessage,
                request.getDescription(false),
                errors
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileUploadException.class)
    @ApiResponse(responseCode = "400", description = "File Upload Error",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException ex, WebRequest request) {
        return buildErrorResponse(ex, "File Upload Error", HttpStatus.BAD_REQUEST, request);
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ApiResponse(responseCode = "400", description = "File Size Exceeded",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, WebRequest request) {
        return buildErrorResponse(ex, "File size exceeds the maximum allowed limit", 
                HttpStatus.BAD_REQUEST, request);
    }
    
    @ExceptionHandler(MultipartException.class)
    @ApiResponse(responseCode = "400", description = "Multipart File Error",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleMultipartException(
            MultipartException ex, WebRequest request) {
        return buildErrorResponse(ex, "Error processing multipart file upload", 
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex, String message, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    public static class ErrorResponse {
        private final String timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final String path;

        public ErrorResponse(String timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public String getPath() {
            return path;
        }
    }

    public static class ValidationErrorResponse extends ErrorResponse {
        private final Map<String, String> fieldErrors;

        public ValidationErrorResponse(String timestamp, int status, String error, String message, String path,
                                       Map<String, String> fieldErrors) {
            super(timestamp, status, error, message, path);
            this.fieldErrors = fieldErrors;
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }
    }
}