package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundInCollectionException;
import pl.skyterix.sadsky.exception.TargetRecordIsTheSameAsSourceException;

import java.util.UUID;

@RequiredArgsConstructor
class RefuseUserPendingInviteAdapter implements RefuseUserPendingInvitePort {

    private final UserRepositoryPort userRepositoryAdapter;

    @Override
    public void refuseUserPendingInvite(UUID userId, UUID friendId) {
        if (userId.equals(friendId)) {
            throw new TargetRecordIsTheSameAsSourceException(Errors.TARGET_RECORD_IS_THE_SAME_AS_SOURCE.getErrorMessage());
        }

        User user = userRepositoryAdapter.findByUserId(userId);
        User friend = userRepositoryAdapter.findByUserId(friendId);

        if (!user.getFriendPendingInvites().contains(friend)) {
            throw new RecordNotFoundInCollectionException(Errors.NO_RECORD_FOUND_IN_COLLECTION.getErrorMessage(friendId));
        }

        // Remove invites
        user.getFriendPendingInvites().remove(friend);
        friend.getFriendSentInvites().remove(user);

        userRepositoryAdapter.updateUser(user);
        userRepositoryAdapter.updateUser(friend);
    }
}
