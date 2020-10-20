package pl.skyterix.sadsky.user.domain;

import java.util.UUID;

interface RemoveUserFromFriendsToPort {

    void removeUserFromFriendsTo(UUID userId, UUID friendId);

}
