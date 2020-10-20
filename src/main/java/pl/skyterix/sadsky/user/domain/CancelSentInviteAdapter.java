package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.exception.TargetRecordIsTheSameAsSourceException;

import java.util.UUID;

@RequiredArgsConstructor
class CancelSentInviteAdapter implements CancelSentInvitePort {

    private final UserRepositoryPort userRepositoryAdapter;

    @Override
    public void cancelSentInvite(UUID userId, UUID friendId) {
        if (userId.equals(friendId))
            throw new TargetRecordIsTheSameAsSourceException(Errors.TARGET_RECORD_IS_THE_SAME_AS_SOURCE.getErrorMessage());

        User user = userRepositoryAdapter.findByUserId(userId);
        User friend = userRepositoryAdapter.findByUserId(friendId);

        if (!user.getFriendSentInvites().contains(friend))
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(friendId));

        // Remove user from friends to
        user.getFriendSentInvites().remove(friend);
        friend.getFriendPendingInvites().remove(user);

        userRepositoryAdapter.updateUser(user);
        userRepositoryAdapter.updateUser(friend);
    }
}
