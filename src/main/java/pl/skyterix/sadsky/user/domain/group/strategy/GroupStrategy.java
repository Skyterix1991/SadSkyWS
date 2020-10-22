package pl.skyterix.sadsky.user.domain.group.strategy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import pl.skyterix.sadsky.user.domain.group.Permissions;
import pl.skyterix.sadsky.user.domain.jackson.GroupStrategyDeserializer;

import java.util.List;

/**
 * @author Skyte
 */
@JsonDeserialize(using = GroupStrategyDeserializer.class)
public interface GroupStrategy {
    /**
     * Get full group name.
     *
     * @return Group name
     */
    String getName();

    /**
     * Get all group permissions self and normal.
     *
     * @return Group permissions
     */
    List<Permissions> getPermissions();

    /**
     * Check if group has a permission self or normal.
     *
     * @param permission SelfPermission or Permission.
     * @return Does this group has that permission.
     */
    boolean hasPermission(Permissions permission);
}
