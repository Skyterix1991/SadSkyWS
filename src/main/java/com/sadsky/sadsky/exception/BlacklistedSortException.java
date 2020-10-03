package com.sadsky.sadsky.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BlacklistedSortException extends RuntimeException {

    public BlacklistedSortException(String message) {
        super(message);
    }

}