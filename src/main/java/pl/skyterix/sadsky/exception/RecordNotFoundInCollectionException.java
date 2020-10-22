package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class RecordNotFoundInCollectionException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public RecordNotFoundInCollectionException(String message) {
        super(message);
    }

}