package pl.skyterix.sadsky.user;

import java.util.UUID;

interface CommandUserFriendsControllerPort {
    void removeUserFromFriends(UUID userId, UUID friendId);

    void addUserToFriendsTo(UUID userId, UUID friendId);

    void removeUserFromFriendsTo(UUID userId, UUID friendId);

    void acceptUserPendingInvite(UUID userId, UUID friendId);

    void refuseUserPendingInvite(UUID userId, UUID friendId);

    void cancelSentInvite(UUID userId, UUID friendId);
}
