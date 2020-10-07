package pl.skyterix.sadsky.user.domain.group.strategy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import pl.skyterix.sadsky.user.domain.group.Permissions;
import pl.skyterix.sadsky.user.domain.jackson.GroupStrategyDeserializer;

import java.util.List;

@JsonDeserialize(using = GroupStrategyDeserializer.class)
public interface GroupStrategy {
    String getName();

    List<Permissions> getPermissions();

    boolean hasPermission(Permissions permission);
}
