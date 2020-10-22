package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.PendingFriendInvitesExceededMaximumException;
import pl.skyterix.sadsky.exception.RecordAlreadyExistsInCollectionException;
import pl.skyterix.sadsky.exception.SentFriendInvitesExceededMaximumException;
import pl.skyterix.sadsky.exception.TargetRecordIsTheSameAsSourceException;

import java.util.UUID;

@RequiredArgsConstructor
class AddUserToFriendsToAdapter implements AddUserToFriendsToPort {

    private final UserRepositoryPort userRepositoryAdapter;

    private final static short MAX_SENT_REQUESTS = 10;
    private final static short MAX_PENDING_REQUESTS = 10;

    @Override
    public void addUserToFriendsTo(UUID userId, UUID friendId) {
        if (userId.equals(friendId)) {
            throw new TargetRecordIsTheSameAsSourceException(Errors.TARGET_RECORD_IS_THE_SAME_AS_SOURCE.getErrorMessage());
        }

        User user = userRepositoryAdapter.findByUserId(userId);
        User friend = userRepositoryAdapter.findByUserId(friendId);

        // Check if invite was already sent
        if (user.getFriendSentInvites().contains(friend)) {
            throw new RecordAlreadyExistsInCollectionException(Errors.RECORD_ALREADY_EXISTS_IN_COLLECTION.getErrorMessage(friendId));
        }

        // Check if friend is already added to friendsTo
        if (user.getFriendsTo().contains(friend)) {
            throw new RecordAlreadyExistsInCollectionException(Errors.RECORD_ALREADY_EXISTS_IN_COLLECTION.getErrorMessage(friendId));
        }

        // Check if current amount of pending invites won't be higher than the limit after this request.
        if (friend.getFriendPendingInvites().size() + 1 > MAX_PENDING_REQUESTS) {
            throw new PendingFriendInvitesExceededMaximumException(Errors.PENDING_FRIEND_INVITES_EXCEEDED_MAXIMUM.getErrorMessage(friendId));
        }

        // Check if current amount of sent invites won't be higher than the limit after this request.
        if (user.getFriendSentInvites().size() + 1 > MAX_SENT_REQUESTS) {
            throw new SentFriendInvitesExceededMaximumException(Errors.SENT_FRIEND_INVITES_EXCEEDED_MAXIMUM.getErrorMessage(userId));
        }

        // Send invites
        user.getFriendSentInvites().add(friend);
        friend.getFriendPendingInvites().add(user);

        userRepositoryAdapter.updateUser(user);
        userRepositoryAdapter.updateUser(friend);
    }
}
