package com.user.user_service.shared.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalFilterException extends RuntimeException {

    public IllegalFilterException(String message) {
        super(message);
    }

}
