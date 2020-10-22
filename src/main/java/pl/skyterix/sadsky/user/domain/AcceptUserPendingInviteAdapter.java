package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.FriendsCountExceededMaximumException;
import pl.skyterix.sadsky.exception.RecordNotFoundInCollectionException;
import pl.skyterix.sadsky.exception.TargetRecordIsTheSameAsSourceException;

import java.util.UUID;

@RequiredArgsConstructor
class AcceptUserPendingInviteAdapter implements AcceptUserPendingInvitePort {

    private final UserRepositoryPort userRepositoryAdapter;

    private final static int MAX_FRIENDS = 50;

    @Override
    public void acceptUserPendingInvite(UUID userId, UUID friendId) {
        if (userId.equals(friendId)) {
            throw new TargetRecordIsTheSameAsSourceException(Errors.TARGET_RECORD_IS_THE_SAME_AS_SOURCE.getErrorMessage());
        }

        User user = userRepositoryAdapter.findByUserId(userId);
        User friend = userRepositoryAdapter.findByUserId(friendId);

        if (!user.getFriendPendingInvites().contains(friend)) {
            throw new RecordNotFoundInCollectionException(Errors.NO_RECORD_FOUND_IN_COLLECTION.getErrorMessage(friendId));
        }

        // Check if current amount of friends invites won't be higher than the limit after this request.
        if (user.getFriends().size() + 1 > MAX_FRIENDS) {
            throw new FriendsCountExceededMaximumException(Errors.FRIENDS_COUNT_EXCEEDED_MAXIMUM.getErrorMessage(userId));
        }

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
