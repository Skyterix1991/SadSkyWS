package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class SentFriendInvitesExceededMaximumException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.NOT_ACCEPTABLE;

    public SentFriendInvitesExceededMaximumException(String message) {
        super(message);
    }

}