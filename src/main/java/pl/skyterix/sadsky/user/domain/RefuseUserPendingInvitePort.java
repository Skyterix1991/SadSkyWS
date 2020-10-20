package pl.skyterix.sadsky.user.domain;

import java.util.UUID;

interface RefuseUserPendingInvitePort {

    void refuseUserPendingInvite(UUID userId, UUID friendId);

}
