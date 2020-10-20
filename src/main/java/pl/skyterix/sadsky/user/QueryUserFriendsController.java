package pl.skyterix.sadsky.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.GroupUnauthorizedException;
import pl.skyterix.sadsky.prediction.QueryPredictionController;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserFacade;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.user.domain.group.Permission;
import pl.skyterix.sadsky.user.domain.group.SelfPermission;
import pl.skyterix.sadsky.user.response.UserDetailsResponse;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class QueryUserFriendsController implements QueryUserFriendsControllerPort {

    private final UserFacade userFacade;
    private final JpaModelMapper jpaModelMapper;

    @Override
    @GetMapping("/friends")
    public List<UserDetailsResponse> getUserFriends(@PathVariable UUID userId) {
        User currentUser = userFacade.getAuthenticatedUser();

        List<UserDTO> users;

        // Check if currentUser has permissions to get user mini friends
        if (currentUser.hasPermission(userId, SelfPermission.GET_SELF_USER_FRIENDS, Permission.GET_USER_FRIENDS))
            if (currentUser.hasPermission(Permission.GET_FULL_USERS))
                users = userFacade.getUserFullFriends(userId);
            else
                users = userFacade.getUserMiniFriends(userId);
        else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

        // Map to response and add hateoas
        return users.stream()
                .map((userDTO) -> jpaModelMapper.mapEntity(userDTO, UserDetailsResponse.class))
                .map(this::addUserRelations)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("/friendsTo")
    public List<UserDetailsResponse> getUserFriendsTo(@PathVariable UUID userId) {
        User currentUser = userFacade.getAuthenticatedUser();

        List<UserDTO> users;

        // Check if currentUser has permissions to get user mini friends to
        if (currentUser.hasPermission(userId, SelfPermission.GET_SELF_USER_FRIENDS_TO, Permission.GET_USER_FRIENDS_TO))
            if (currentUser.hasPermission(Permission.GET_FULL_USERS))
                users = userFacade.getUserFullFriendsTo(userId);
            else
                users = userFacade.getUserMiniFriendsTo(userId);
        else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

        // Map to response and add hateoas
        return users.stream()
                .map((userDTO) -> jpaModelMapper.mapEntity(userDTO, UserDetailsResponse.class))
                .map(this::addUserRelations)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("/pendingInvites")
    public List<UserDetailsResponse> getUserPendingInvites(@PathVariable UUID userId) {
        User currentUser = userFacade.getAuthenticatedUser();

        List<UserDTO> users;

        // Check if currentUser has permissions to get user mini pending invites
        if (currentUser.hasPermission(userId, SelfPermission.GET_SELF_USER_PENDING_INVITES, Permission.GET_USER_PENDING_INVITES))
            if (currentUser.hasPermission(Permission.GET_FULL_USERS))
                users = userFacade.getUserFullPendingInvites(userId);
            else
                users = userFacade.getUserMiniPendingInvites(userId);
        else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

        // Map to response and add hateoas
        return users.stream()
                .map((userDTO) -> jpaModelMapper.mapEntity(userDTO, UserDetailsResponse.class))
                .map(this::addUserRelations)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("/sentInvites")
    public List<UserDetailsResponse> getUserSentInvites(@PathVariable UUID userId) {
        User currentUser = userFacade.getAuthenticatedUser();

        List<UserDTO> users;

        // Check if currentUser has permissions to get user mini sent invites
        if (currentUser.hasPermission(userId, SelfPermission.GET_SELF_USER_SENT_INVITES, Permission.GET_USER_SENT_INVITES))
            if (currentUser.hasPermission(Permission.GET_FULL_USERS))
                users = userFacade.getUserFullSentInvites(userId);
            else
                users = userFacade.getUserMiniSentInvites(userId);
        else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

        // Map to response and add hateoas
        return users.stream()
                .map((userDTO) -> jpaModelMapper.mapEntity(userDTO, UserDetailsResponse.class))
                .map(this::addUserRelations)
                .collect(Collectors.toList());
    }

    private UserDetailsResponse addUserRelations(UserDetailsResponse userDetailsResponse) {
        userDetailsResponse.add(linkTo(methodOn(QueryUserController.class).getUser(userDetailsResponse.getUserId())).withSelfRel());
        userDetailsResponse.add(linkTo(methodOn(QueryUserFriendsController.class).getUserFriends(userDetailsResponse.getUserId())).withRel("userFriends"));
        userDetailsResponse.add(linkTo(methodOn(QueryUserController.class).getUsers(null, null, null, null, null)).withRel("users"));
        userDetailsResponse.add(linkTo(methodOn(QueryPredictionController.class).getUserPredictions(userDetailsResponse.getUserId())).withRel("userPredictions"));
        userDetailsResponse.add(linkTo(methodOn(QueryPredictionController.class).getPrediction(userDetailsResponse.getUserId(), null)).withRel("userPrediction"));

        return userDetailsResponse;
    }

}
