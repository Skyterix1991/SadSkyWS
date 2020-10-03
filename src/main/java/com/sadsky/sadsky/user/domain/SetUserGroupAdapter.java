package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
class SetUserGroupAdapter implements SetUserGroupPort {

    private final UserRepositoryPort userRepositoryAdapter;

    @Override
    public void setUserGroup(UUID userId, GroupStrategy groupStrategy) {
        User user = userRepositoryAdapter.findByUserId(userId);

        user.setGroup(groupStrategy);

        userRepositoryAdapter.updateUser(user);
    }
}
