package pl.skyterix.sadsky.exception;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

/**
 * Class contains error messages enum which can be accessed using getErrorMessage().
 * Messages can contain parenthesis which can be filled using ex. Your id is {0}! and use method getErrorMessage(3);
 * Messages also can contain multiple parenthesis all can be filled using ex. Your id is {0} and your name is {1}! and use method getErrorMessage(new String[]{"3", "John"})
 */
@RequiredArgsConstructor
public enum Errors {

    NO_RECORD_FOUND("Record {0} was not found."),
    RECORD_ALREADY_EXISTS("Record with {0} already exists."),
    UNAUTHORIZED_GROUP("Your group {0} is not authorized for this action."),
    GROUP_NOT_FOUND("Group with name {0} was not found."),
    SORT_NOT_ALLOWED_ON_FIELD("Sorting by {0} is not allowed."),
    AGE_NOT_MEETING_REQUIRED("Your age must be between 16 and 100 years."),
    PREDICTION_RESULT_NOT_READY_TO_GENERATE("Prediction result is not ready to generate.");

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