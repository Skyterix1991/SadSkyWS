package pl.skyterix.sadsky.user.domain.group.strategy;

import pl.skyterix.sadsky.user.domain.group.GroupPermissions;
import pl.skyterix.sadsky.user.domain.group.Permissions;

import java.util.List;

/**
 * @author Skyte
 */
public class AdminGroup implements GroupStrategy {

    private static final List<Permissions> PERMISSIONS = GroupPermissions.ADMIN_PERMISSIONS;

    @Override
    public String getName() {
        return "Administrator";
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
