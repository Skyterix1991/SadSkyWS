package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class PredictionResultIsAlreadyGeneratedException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.CONFLICT;

    public PredictionResultIsAlreadyGeneratedException(String message) {
        super(message);
    }

}