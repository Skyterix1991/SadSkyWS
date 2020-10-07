package pl.skyterix.sadsky.exception;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

@RequiredArgsConstructor
public enum Errors {

    NO_RECORD_FOUND("Record {0} was not found."),
    RECORD_ALREADY_EXISTS("Record with {0} already exists."),
    UNAUTHORIZED_GROUP("Your group {0} is not authorized for this action."),
    GROUP_NOT_FOUND("Group with name {0} was not found."),
    SORT_NOT_ALLOWED_ON_FIELD("Sorting by {0} is not allowed."),
    AGE_NOT_MEETING_REQUIRED("Your age must be between 16 and 100 years.");

    private final String errorMessage;

    public String getErrorMessage(Object[] data) {
        return MessageFormat.format(errorMessage, data);
    }

    public String getErrorMessage(Object data) {
        return MessageFormat.format(errorMessage, data);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}