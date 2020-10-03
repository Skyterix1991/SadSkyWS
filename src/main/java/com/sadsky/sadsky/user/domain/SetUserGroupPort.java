package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;

import java.util.UUID;

interface SetUserGroupPort {

    void setUserGroup(UUID userId, GroupStrategy groupStrategy);

}
