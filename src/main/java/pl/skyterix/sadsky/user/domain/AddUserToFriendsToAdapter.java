package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordAlreadyExistsException;
import pl.skyterix.sadsky.exception.TargetRecordIsTheSameAsSourceException;

import java.util.UUID;

@RequiredArgsConstructor
class AddUserToFriendsToAdapter implements AddUserToFriendsToPort {

    private final UserRepositoryPort userRepositoryAdapter;

    @Override
    public void addUserToFriendsTo(UUID userId, UUID friendId) {
        if (userId.equals(friendId))
            throw new TargetRecordIsTheSameAsSourceException(Errors.TARGET_RECORD_IS_THE_SAME_AS_SOURCE.getErrorMessage());

        User user = userRepositoryAdapter.findByUserId(userId);
        User friend = userRepositoryAdapter.findByUserId(friendId);

        if (user.getFriendSentInvites().contains(friend))
            throw new RecordAlreadyExistsException(Errors.RECORD_ALREADY_EXISTS.getErrorMessage(friendId));

        // Send invites
        user.getFriendSentInvites().add(friend);
        friend.getFriendPendingInvites().add(user);

        userRepositoryAdapter.updateUser(user);
        userRepositoryAdapter.updateUser(friend);
    }
}
