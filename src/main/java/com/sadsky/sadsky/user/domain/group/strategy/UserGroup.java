package com.sadsky.sadsky.user.domain.group.strategy;

import com.sadsky.sadsky.user.domain.group.GroupPermissions;
import com.sadsky.sadsky.user.domain.group.Permissions;

import java.util.List;

public class UserGroup implements GroupStrategy {

    private static final List<Permissions> PERMISSIONS = GroupPermissions.USER_PERMISSIONS;

    @Override
    public String getName() {
        return "User";
    }

    @Override
    public List<Permissions> getPermissions() {
        return PERMISSIONS;
    }

    @Override
    public boolean hasPermission(Permissions permission) {
        return PERMISSIONS.contains(permission);
    }
}
