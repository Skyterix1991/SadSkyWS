package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class BlacklistedSortException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public BlacklistedSortException(String message) {
        super(message);
    }

}