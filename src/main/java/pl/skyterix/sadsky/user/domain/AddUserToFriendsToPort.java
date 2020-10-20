package pl.skyterix.sadsky.user.domain;

import java.util.UUID;

interface AddUserToFriendsToPort {

    void addUserToFriendsTo(UUID userId, UUID friendId);

}
