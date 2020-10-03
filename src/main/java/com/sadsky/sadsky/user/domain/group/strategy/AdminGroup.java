package com.sadsky.sadsky.user.domain.group.strategy;

import com.sadsky.sadsky.user.domain.group.GroupPermissions;
import com.sadsky.sadsky.user.domain.group.Permissions;

import java.util.List;

public class AdminGroup implements GroupStrategy {

    private static final List<Permissions> PERMISSIONS = GroupPermissions.ADMIN_PERMISSIONS;

    @Override
    public String getName() {
        return "Admin";
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
