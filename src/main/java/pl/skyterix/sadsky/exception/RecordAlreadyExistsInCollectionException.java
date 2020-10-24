package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class RecordAlreadyExistsInCollectionException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.CONFLICT;

    public RecordAlreadyExistsInCollectionException(String message) {
        super(message);
    }

}