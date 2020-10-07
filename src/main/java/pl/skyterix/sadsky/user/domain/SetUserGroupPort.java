package pl.skyterix.sadsky.user.domain;

import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;

import java.util.UUID;

interface SetUserGroupPort {

    void setUserGroup(UUID userId, GroupStrategy groupStrategy);

}
