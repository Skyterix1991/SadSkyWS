package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class DayDeadlineException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.NOT_ACCEPTABLE;

    public DayDeadlineException(String message) {
        super(message);
    }

}