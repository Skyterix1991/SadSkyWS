package pl.skyterix.sadsky.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class PredictionResultIsNotReadyToGenerateException extends RuntimeException {

    public PredictionResultIsNotReadyToGenerateException(String message) {
        super(message);
    }

}