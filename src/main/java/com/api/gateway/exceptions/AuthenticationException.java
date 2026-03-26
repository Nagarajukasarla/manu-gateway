package com.api.gateway.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public final class AuthenticationException extends RuntimeException {

    private final HttpStatus status = HttpStatus.UNAUTHORIZED;
    private final ResponseCode responseCode;

    public AuthenticationException(String message, ResponseCode responseCode) {
        super(message);
        this.responseCode = responseCode;
    }
}