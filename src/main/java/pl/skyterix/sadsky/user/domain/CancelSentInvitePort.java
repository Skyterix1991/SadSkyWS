package pl.skyterix.sadsky.user.domain;

import java.util.UUID;

interface CancelSentInvitePort {

    void cancelSentInvite(UUID userId, UUID friendId);

}
