package pl.skyterix.sadsky.user.domain;

import java.util.UUID;

interface RemoveUserFromFriendsPort {

    void removeUserFromFriends(UUID userId, UUID friendId);

}
