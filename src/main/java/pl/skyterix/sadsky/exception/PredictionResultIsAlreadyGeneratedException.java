package pl.skyterix.sadsky.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PredictionResultIsAlreadyGeneratedException extends RuntimeException {

    public PredictionResultIsAlreadyGeneratedException(String message) {
        super(message);
    }

}