package com.Lamicons.CurriculumService.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a file upload validation fails.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
    
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}