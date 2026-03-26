package com.api.gateway.exceptions;

import lombok.Getter;

@Getter
public enum ResponseCode {

    // Generic
    MISSING_TOKEN("AUTH_001", "Missing Authorization Header"),

    // Token
    INVALID_VALID("AUTH_002", "Invalid Token"),
    EXPIRED_TOKEN("AUTH_003", "Token Expired"),

    ACCESS_DENIED("AUTH_004", "Access Denied"),   // Authorization (Future use)
    SUCCESS("AUTH_005", "Valid Token");

    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

