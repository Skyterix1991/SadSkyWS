package pl.skyterix.sadsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class PendingFriendInvitesExceededMaximumException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.NOT_ACCEPTABLE;

    public PendingFriendInvitesExceededMaximumException(String message) {
        super(message);
    }

}