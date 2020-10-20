package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.exception.TargetRecordIsTheSameAsSourceException;

import java.util.UUID;

@RequiredArgsConstructor
class AcceptUserPendingInviteAdapter implements AcceptUserPendingInvitePort {

    private final UserRepositoryPort userRepositoryAdapter;

    @Override
    public void acceptUserPendingInvite(UUID userId, UUID friendId) {
        if (userId == friendId)
            throw new TargetRecordIsTheSameAsSourceException(Errors.TARGET_RECORD_IS_THE_SAME_AS_SOURCE.getErrorMessage());

        User user = userRepositoryAdapter.findByUserId(userId);
        User friend = userRepositoryAdapter.findByUserId(friendId);

        if (!user.getFriendPendingInvites().contains(friend))
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(friendId));

        // Remove invites
        user.getFriendPendingInvites().remove(friend);
        friend.getFriendSentInvites().remove(user);

        // Add friends
        user.getFriends().add(friend);
        friend.getFriendsTo().add(user);

        userRepositoryAdapter.updateUser(user);
        userRepositoryAdapter.updateUser(friend);
    }
}
