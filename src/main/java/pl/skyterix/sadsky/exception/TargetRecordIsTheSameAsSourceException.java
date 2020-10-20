package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class TargetRecordIsTheSameAsSourceException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public TargetRecordIsTheSameAsSourceException(String message) {
        super(message);
    }

}