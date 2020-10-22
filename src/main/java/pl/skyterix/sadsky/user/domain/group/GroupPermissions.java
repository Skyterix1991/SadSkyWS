package pl.skyterix.sadsky.user.domain.group;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static pl.skyterix.sadsky.user.domain.group.SelfPermission.ACCEPT_SELF_USER_PENDING_INVITE;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.ADD_SELF_USER_TO_FRIENDS_TO;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.CANCEL_SELF_SENT_INVITE;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.DELETE_SELF_USER;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GENERATE_SELF_PREDICTION_RESULT;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GET_FULL_SELF_USER;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GET_SELF_USER_FRIENDS;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GET_SELF_USER_FRIENDS_TO;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GET_SELF_USER_PENDING_INVITES;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GET_SELF_USER_PREDICTION;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GET_SELF_USER_PREDICTIONS;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GET_SELF_USER_SENT_INVITES;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.REFUSE_SELF_USER_PENDING_INVITE;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.REMOVE_SELF_USER_FROM_FRIENDS;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.REMOVE_SELF_USER_FROM_FRIENDS_TO;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.REPLACE_SELF_USER;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.SET_SELF_PREDICTION_DAY_EMOTIONS;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.UPDATE_SELF_USER;

/**
 * Permissions for each of groups.
 * You can add permissions from Permission class and SelfPermission class.
 *
 * @author Skyte
 */
public class GroupPermissions {

    public static final List<Permissions> USER_PERMISSIONS = Arrays.asList(
            // User
            GET_FULL_SELF_USER,
            DELETE_SELF_USER,
            UPDATE_SELF_USER,
            REPLACE_SELF_USER,
            GET_SELF_USER_PREDICTIONS,
            GET_SELF_USER_PREDICTION,
            GENERATE_SELF_PREDICTION_RESULT,
            SET_SELF_PREDICTION_DAY_EMOTIONS,

            GET_SELF_USER_FRIENDS,
            GET_SELF_USER_FRIENDS_TO,
            GET_SELF_USER_SENT_INVITES,
            GET_SELF_USER_PENDING_INVITES,

            ADD_SELF_USER_TO_FRIENDS_TO,
            REMOVE_SELF_USER_FROM_FRIENDS_TO,
            REMOVE_SELF_USER_FROM_FRIENDS,
            ACCEPT_SELF_USER_PENDING_INVITE,
            REFUSE_SELF_USER_PENDING_INVITE,
            CANCEL_SELF_SENT_INVITE
    );

    /**
     * Contains all available permissions.
     */
    public static final List<Permissions> ADMIN_PERMISSIONS = Arrays.asList(Stream.of(Permission.values(), SelfPermission.values()).flatMap(Stream::of)
            .toArray(Permissions[]::new));
}
