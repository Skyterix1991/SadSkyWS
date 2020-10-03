package com.sadsky.sadsky.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class GroupUnauthorizedException extends RuntimeException {

    public GroupUnauthorizedException(String message) {
        super(message);
    }

}