package com.sadsky.sadsky.user.domain.group;

public enum Permission implements Permissions {
    // User permissions
    GET_FULL_USER,
    GET_MINI_USER,

    GET_FULL_USERS,
    GET_MINI_USERS,

    CREATE_USER,

    DELETE_USER,

    UPDATE_USER,

    REPLACE_USER,

    // Group permissions
    ASSIGN_GROUP
}
