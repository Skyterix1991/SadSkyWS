package pl.skyterix.sadsky.user;


import pl.skyterix.sadsky.user.response.UserDetailsResponse;

import java.util.List;
import java.util.UUID;

interface QueryUserFriendsControllerPort {
    List<UserDetailsResponse> getUserFriends(UUID userId);

    List<UserDetailsResponse> getUserFriendsTo(UUID userId);

    List<UserDetailsResponse> getUserPendingInvites(UUID userId);

    List<UserDetailsResponse> getUserSentInvites(UUID userId);
}
