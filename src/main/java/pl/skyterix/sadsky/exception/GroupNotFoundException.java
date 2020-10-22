package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class GroupNotFoundException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public GroupNotFoundException(String message) {
        super(message);
    }

}