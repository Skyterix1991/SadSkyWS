package pl.skyterix.sadsky.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class PredictionIsExpiredException extends RuntimeException {

    public PredictionIsExpiredException(String message) {
        super(message);
    }

}