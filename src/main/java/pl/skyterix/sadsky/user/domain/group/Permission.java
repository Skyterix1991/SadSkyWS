package pl.skyterix.sadsky.user.domain.group;

/**
 * Permissions to use on any other user.
 */
public enum Permission implements Permissions {
    // User permissions
    GET_FULL_USER,
    GET_MINI_USER,

    GET_FULL_USERS,
    GET_MINI_USERS,

    DELETE_USER,

    UPDATE_USER,

    REPLACE_USER,

    GET_USER_FRIENDS,
    GET_USER_FRIENDS_TO,
    GET_USER_SENT_INVITES,
    GET_USER_PENDING_INVITES,

    ADD_USER_TO_FRIENDS_TO,
    REMOVE_USER_FROM_FRIENDS_TO,
    REMOVE_USER_FROM_FRIENDS,
    ACCEPT_USER_PENDING_INVITE,
    REFUSE_USER_PENDING_INVITE,
    CANCEL_SENT_INVITE,

    // Group permissions
    ASSIGN_GROUP,

    // Prediction permissions
    GET_USER_PREDICTIONS,
    GET_USER_PREDICTION,

    GENERATE_PREDICTION_RESULT,
    SET_PREDICTION_DAY_EMOTIONS
}
