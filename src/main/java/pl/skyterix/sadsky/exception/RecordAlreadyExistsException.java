package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class RecordAlreadyExistsException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.CONFLICT;

    public RecordAlreadyExistsException(String message) {
        super(message);
    }

}