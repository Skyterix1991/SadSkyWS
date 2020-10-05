package pl.skyterix.sadsky.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AgeNotMeetingRequired extends RuntimeException {

    public AgeNotMeetingRequired(String message) {
        super(message);
    }

}