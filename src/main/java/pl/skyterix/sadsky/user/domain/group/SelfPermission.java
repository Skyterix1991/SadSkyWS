package pl.skyterix.sadsky.user.domain.group;

/**
 * Permissions to only use on yourself.
 */
public enum SelfPermission implements Permissions {
    // User permissions
    GET_FULL_SELF_USER,
    GET_MINI_SELF_USER,

    DELETE_SELF_USER,

    UPDATE_SELF_USER,

    REPLACE_SELF_USER,

    // Prediction permissions
    GET_SELF_USER_PREDICTIONS,
    GET_SELF_USER_PREDICTION,

    GENERATE_SELF_PREDICTION_RESULT,
    SET_SELF_PREDICTION_DAY_EMOTIONS
}
