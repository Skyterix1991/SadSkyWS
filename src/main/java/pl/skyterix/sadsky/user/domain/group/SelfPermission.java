package pl.skyterix.sadsky.user.domain.group;

/**
 * Permissions to only use on yourself.
 *
 * @author Skyte
 */
public enum SelfPermission implements Permissions {
    // User permissions
    GET_FULL_SELF_USER,
    GET_MINI_SELF_USER,

    DELETE_SELF_USER,

    UPDATE_SELF_USER,

    REPLACE_SELF_USER,

    GET_SELF_USER_FRIENDS,
    GET_SELF_USER_FRIENDS_TO,
    GET_SELF_USER_SENT_INVITES,
    GET_SELF_USER_PENDING_INVITES,

    ADD_SELF_USER_TO_FRIENDS_TO,
    REMOVE_SELF_USER_FROM_FRIENDS_TO,
    REMOVE_SELF_USER_FROM_FRIENDS,
    ACCEPT_SELF_USER_PENDING_INVITE,
    REFUSE_SELF_USER_PENDING_INVITE,
    CANCEL_SELF_SENT_INVITE,

    // Prediction permissions
    GET_SELF_USER_PREDICTIONS,
    GET_SELF_USER_PREDICTION,

    GENERATE_SELF_PREDICTION_RESULT,
    SET_SELF_PREDICTION_DAY_EMOTIONS
}
