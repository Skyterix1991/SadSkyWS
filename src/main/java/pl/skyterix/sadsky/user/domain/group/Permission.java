package pl.skyterix.sadsky.user.domain.group;

public enum Permission implements Permissions {
    // User permissions
    GET_FULL_USER,
    GET_MINI_USER,

    GET_FULL_USERS,
    GET_MINI_USERS,

    DELETE_USER,

    UPDATE_USER,

    REPLACE_USER,

    // Group permissions
    ASSIGN_GROUP,

    // Prediction permissions
    GET_USER_PREDICTIONS,
    GET_USER_PREDICTION,

    GENERATE_PREDICTION_RESULT,
    SET_PREDICTION_DAY_EMOTIONS
}
