package com.user.user_service.shared.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
/**
 * Exception thrown when a user tries to access a resource they do not have permission to access.
 * This exception is typically used in the context of authorization checks.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
    
}
