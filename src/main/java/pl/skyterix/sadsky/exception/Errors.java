package pl.skyterix.sadsky.exception;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

/**
 * Class contains error messages enum which can be accessed using getErrorMessage().
 * Messages can contain parenthesis which can be filled using ex. Your id is {0}! and use method getErrorMessage(3);
 * Messages also can contain multiple parenthesis all can be filled using ex. Your id is {0} and your name is {1}! and use method getErrorMessage(new String[]{"3", "John"})
 * It would be nice to also indicate what value was filled for ex. [uuid={0}].
 * <p>
 * Each exception that wants to use those error messages should be registered in RestExceptionControllerAdvice.
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public enum Errors {

    NO_RECORD_FOUND("Record [uuid={0}] was not found."),
    RECORD_ALREADY_EXISTS("Record [uuid={0}] already exists."),
    UNAUTHORIZED_GROUP("Group [group={0}] don't have permission to do that action."),
    GROUP_NOT_FOUND("Group [group={0}] was not found."),
    SORT_NOT_ALLOWED_ON_FIELD("Sorting by [field={0}] is not allowed."),
    AGE_NOT_MEETING_REQUIRED("Your age must be between 16 years old and 100 years old."),
    PREDICTION_RESULT_NOT_READY_TO_GENERATE("Result is not ready to generate."),
    PREDICTION_RESULT_IS_ALREADY_GENERATED("Result is already generated."),
    PREDICTION_IS_EXPIRED("Prediction [uuid={0}] expired."),
    DAY_DEADLINE_EXCEPTION("Day deadline reached."),
    BAD_REQUEST("Given request data is invalid."),
    UNIDENTIFIED("Unknown error occurred."),
    TARGET_RECORD_IS_THE_SAME_AS_SOURCE("Target record is the same as source record."),
    RECORD_ALREADY_EXISTS_IN_COLLECTION("Record [uuid={0}] already exists in collection."),
    PENDING_FRIEND_INVITES_EXCEEDED_MAXIMUM("User [uuid={0}] pending invites limit was reached."),
    SENT_FRIEND_INVITES_EXCEEDED_MAXIMUM("User [uuid={0}] sent invites limit was reached."),
    FRIENDS_COUNT_EXCEEDED_MAXIMUM("User [uuid={0}] friends limit was reached."),
    NO_RECORD_FOUND_IN_COLLECTION("Record [uuid={0}] was not found in collection.");

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