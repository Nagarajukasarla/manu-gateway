package com.api.gateway.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthorizationException extends RuntimeException {

    private final HttpStatus status = HttpStatus.FORBIDDEN;
    private final ResponseCode responseCode;

    public AuthorizationException(String message, ResponseCode responseCode) {
        super(message);
        this.responseCode = responseCode;
    }
}
