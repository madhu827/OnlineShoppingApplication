package com.onlineshopping.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when a JWT refresh token is expired, invalid, or not found. Returns HTTP 401. */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for token [%s]: %s", token, message));
    }
}

