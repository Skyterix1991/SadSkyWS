package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class GroupUnauthorizedException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.UNAUTHORIZED;

    public GroupUnauthorizedException(String message) {
        super(message);
    }

}