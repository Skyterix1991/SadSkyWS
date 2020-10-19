package pl.skyterix.sadsky.exception;

import org.springframework.http.HttpStatus;

interface RestException {

    HttpStatus getStatus();

    String getMessage();
}
