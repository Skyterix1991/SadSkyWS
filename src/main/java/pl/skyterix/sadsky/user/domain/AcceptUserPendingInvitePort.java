package pl.skyterix.sadsky.user.domain;

import java.util.UUID;

interface AcceptUserPendingInvitePort {

    void acceptUserPendingInvite(UUID userId, UUID friendId);

}
