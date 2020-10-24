package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class AgeNotMeetingRequiredException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public AgeNotMeetingRequiredException(String message) {
        super(message);
    }

}