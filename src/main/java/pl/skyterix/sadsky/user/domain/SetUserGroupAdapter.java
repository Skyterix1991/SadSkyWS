package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;

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
