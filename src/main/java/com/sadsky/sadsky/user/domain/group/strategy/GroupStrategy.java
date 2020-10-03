package com.sadsky.sadsky.user.domain.group.strategy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sadsky.sadsky.user.domain.group.Permissions;
import com.sadsky.sadsky.user.domain.jackson.GroupStrategyDeserializer;

import java.util.List;

@JsonDeserialize(using = GroupStrategyDeserializer.class)
public interface GroupStrategy {
    String getName();

    List<Permissions> getPermissions();

    boolean hasPermission(Permissions permission);
}
