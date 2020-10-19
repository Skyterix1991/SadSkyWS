package pl.skyterix.sadsky.user.domain.group;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static pl.skyterix.sadsky.user.domain.group.SelfPermission.DELETE_SELF_USER;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GENERATE_SELF_PREDICTION_RESULT;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GET_FULL_SELF_USER;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GET_SELF_USER_PREDICTION;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.GET_SELF_USER_PREDICTIONS;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.REPLACE_SELF_USER;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.SET_SELF_PREDICTION_DAY_EMOTIONS;
import static pl.skyterix.sadsky.user.domain.group.SelfPermission.UPDATE_SELF_USER;

/**
 * Permissions for each of groups.
 * You can add permissions from Permission class and SelfPermission class.
 */
public class GroupPermissions {

    public static final List<Permissions> USER_PERMISSIONS = Arrays.asList(
            // User
            GET_FULL_SELF_USER,
            Permission.GET_MINI_USER,
            Permission.GET_MINI_USERS,
            DELETE_SELF_USER,
            UPDATE_SELF_USER,
            REPLACE_SELF_USER,
            GET_SELF_USER_PREDICTIONS,
            GET_SELF_USER_PREDICTION,
            GENERATE_SELF_PREDICTION_RESULT,
            SET_SELF_PREDICTION_DAY_EMOTIONS
    );

    /**
     * Contains all available permissions.
     */
    public static final List<Permissions> ADMIN_PERMISSIONS = Arrays.asList(Stream.of(Permission.values(), SelfPermission.values()).flatMap(Stream::of)
            .toArray(Permissions[]::new));
}
