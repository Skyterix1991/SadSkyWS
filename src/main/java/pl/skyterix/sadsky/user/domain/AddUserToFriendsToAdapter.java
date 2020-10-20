package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordAlreadyExistsException;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.exception.TargetRecordIsTheSameAsSourceException;

import java.util.UUID;

@RequiredArgsConstructor
class AddUserToFriendsToAdapter implements AddUserToFriendsToPort {

    private final UserRepositoryPort userRepositoryAdapter;

    @Override
    public void addUserToFriendsTo(UUID userId, UUID friendId) {
        if (userId == friendId)
            throw new TargetRecordIsTheSameAsSourceException(Errors.TARGET_RECORD_IS_THE_SAME_AS_SOURCE.getErrorMessage());

        User user = userRepositoryAdapter.findByUserId(userId);
        User friend = userRepositoryAdapter.findByUserId(friendId);

        if (friend.getFriendPendingInvites().contains(user))
            throw new RecordNotFoundException(Errors.RECORD_ALREADY_EXISTS.getErrorMessage(friendId));

        if (user.getFriendSentInvites().contains(friend))
            throw new RecordAlreadyExistsException(Errors.RECORD_ALREADY_EXISTS.getErrorMessage(friendId));

        // Send invites
        user.getFriendSentInvites().add(friend);
        friend.getFriendPendingInvites().add(user);

        userRepositoryAdapter.updateUser(user);
        userRepositoryAdapter.updateUser(friend);
    }
}
