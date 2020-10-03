package com.sadsky.sadsky.user.domain.group;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.sadsky.sadsky.user.domain.group.Permission.GET_MINI_USER;
import static com.sadsky.sadsky.user.domain.group.Permission.GET_MINI_USERS;
import static com.sadsky.sadsky.user.domain.group.SelfPermission.DELETE_SELF_USER;
import static com.sadsky.sadsky.user.domain.group.SelfPermission.GET_FULL_SELF_USER;
import static com.sadsky.sadsky.user.domain.group.SelfPermission.REPLACE_SELF_USER;
import static com.sadsky.sadsky.user.domain.group.SelfPermission.UPDATE_SELF_USER;

public class GroupPermissions {

    public static final List<Permissions> USER_PERMISSIONS = Arrays.asList(
            // User
            GET_FULL_SELF_USER,
            GET_MINI_USER,
            GET_MINI_USERS,
            DELETE_SELF_USER,
            UPDATE_SELF_USER,
            REPLACE_SELF_USER
    );

    public static final List<Permissions> ADMIN_PERMISSIONS = Arrays.asList(Stream.of(Permission.values(), SelfPermission.values()).flatMap(Stream::of)
            .toArray(Permissions[]::new));
}
