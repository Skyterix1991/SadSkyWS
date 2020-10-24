package pl.skyterix.sadsky.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.GroupUnauthorizedException;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserFacade;
import pl.skyterix.sadsky.user.domain.group.Permission;
import pl.skyterix.sadsky.user.domain.group.SelfPermission;

import java.util.UUID;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommandUserFriendsController implements CommandUserFriendsControllerPort {

    private final UserFacade userFacade;

    @Override
    @DeleteMapping("/friends/{friendId}")
    public void removeUserFromFriends(@PathVariable UUID userId, @PathVariable UUID friendId) {
        User currentUser = userFacade.getAuthenticatedUser();

        // Checks if currentUser has permission to remove user form friends
        if (currentUser.hasPermission(userId, SelfPermission.REMOVE_SELF_USER_FROM_FRIENDS, Permission.REMOVE_USER_FROM_FRIENDS)) {
            userFacade.removeUserFromFriends(userId, friendId);
        } else {
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
        }
    }

    @Override
    @PostMapping("/friendsTo/{friendId}")
    public void addUserToFriendsTo(@PathVariable UUID userId, @PathVariable UUID friendId) {
        User currentUser = userFacade.getAuthenticatedUser();

        // Checks if currentUser has permission to add that user to friends to
        if (currentUser.hasPermission(userId, SelfPermission.ADD_SELF_USER_TO_FRIENDS_TO, Permission.ADD_USER_TO_FRIENDS_TO)) {
            userFacade.addUserToFriendsTo(userId, friendId);
        } else {
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
        }
    }

    @Override
    @DeleteMapping("/friendsTo/{friendId}")
    public void removeUserFromFriendsTo(@PathVariable UUID userId, @PathVariable UUID friendId) {
        User currentUser = userFacade.getAuthenticatedUser();

        // Checks if currentUser has permission to remove that user from friends to
        if (currentUser.hasPermission(userId, SelfPermission.REMOVE_SELF_USER_FROM_FRIENDS_TO, Permission.REMOVE_USER_FROM_FRIENDS_TO)) {
            userFacade.removeUserFromFriendsTo(userId, friendId);
        } else {
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
        }
    }

    @Override
    @PostMapping("/pendingInvites/{friendId}")
    public void acceptUserPendingInvite(@PathVariable UUID userId, @PathVariable UUID friendId) {
        User currentUser = userFacade.getAuthenticatedUser();

        // Checks if currentUser has permission to accept user in pending invites
        if (currentUser.hasPermission(userId, SelfPermission.ACCEPT_SELF_USER_PENDING_INVITE, Permission.ACCEPT_USER_PENDING_INVITE)) {
            userFacade.acceptUserPendingInvite(userId, friendId);
        } else {
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
        }
    }

    @Override
    @DeleteMapping("/pendingInvites/{friendId}")
    public void refuseUserPendingInvite(@PathVariable UUID userId, @PathVariable UUID friendId) {
        User currentUser = userFacade.getAuthenticatedUser();

        // Checks if currentUser has permission to refuse user in pending invites
        if (currentUser.hasPermission(userId, SelfPermission.REFUSE_SELF_USER_PENDING_INVITE, Permission.REFUSE_USER_PENDING_INVITE)) {
            userFacade.refuseUserPendingInvite(userId, friendId);
        } else {
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
        }
    }

    @Override
    @DeleteMapping("/sentInvites/{friendId}")
    public void cancelSentInvite(@PathVariable UUID userId, @PathVariable UUID friendId) {
        User currentUser = userFacade.getAuthenticatedUser();

        // Checks if currentUser has permission to cancel sent user invite
        if (currentUser.hasPermission(userId, SelfPermission.CANCEL_SELF_SENT_INVITE, Permission.CANCEL_SENT_INVITE)) {
            userFacade.cancelSentInvite(userId, friendId);
        } else {
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
        }
    }
}
