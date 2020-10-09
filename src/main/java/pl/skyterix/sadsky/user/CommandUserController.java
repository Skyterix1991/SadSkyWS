package pl.skyterix.sadsky.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.GroupUnauthorizedException;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserFacade;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.user.domain.group.Permission;
import pl.skyterix.sadsky.user.domain.group.SelfPermission;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;
import pl.skyterix.sadsky.user.request.UserDetailsRequest;
import pl.skyterix.sadsky.user.request.UserReplaceRequest;
import pl.skyterix.sadsky.user.request.UserUpdateRequest;
import pl.skyterix.sadsky.util.JpaModelMapper;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommandUserController implements CommandUserControllerPort {

    private final UserFacade userFacade;
    private final JpaModelMapper jpaModelMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody @Validated UserDetailsRequest userDetailsRequest, HttpServletResponse response) {
        UserDTO userDTO = jpaModelMapper.mapEntity(userDetailsRequest, UserDTO.class);

        UUID userId = userFacade.createUser(userDTO);

        response.addHeader("Id", userId.toString());
    }

    @Override
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable UUID userId) {
        User currentUser = userFacade.getAuthenticatedUser();

        // Checks if currentUser has permission to delete that user
        if (currentUser.hasPermission(userId, SelfPermission.DELETE_SELF_USER, Permission.DELETE_USER)) {
            userFacade.deleteUser(userId);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

    @Override
    @PatchMapping("/{userId}")
    public void updateUser(@PathVariable UUID userId, @RequestBody @Validated UserUpdateRequest userUpdateRequest) {
        User currentUser = userFacade.getAuthenticatedUser();

        UserDTO userDTO = jpaModelMapper.mapEntity(userUpdateRequest, UserDTO.class);

        // Checks if currentUser has permission to update that user
        if (currentUser.hasPermission(userId, SelfPermission.UPDATE_SELF_USER, Permission.UPDATE_USER)) {
            userFacade.updateUser(userId, userDTO);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

    @Override
    @PutMapping("/{userId}")
    public void replaceUser(@PathVariable UUID userId, @RequestBody @Validated UserReplaceRequest userReplaceRequest) {
        User currentUser = userFacade.getAuthenticatedUser();

        UserDTO userDTO = jpaModelMapper.mapEntity(userReplaceRequest, UserDTO.class);

        // Checks if currentUser has permission to replace that user
        if (currentUser.hasPermission(userId, SelfPermission.REPLACE_SELF_USER, Permission.REPLACE_USER)) {
            userFacade.replaceUser(userId, userDTO);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

    @Override
    @PostMapping("/{userId}/group")
    public void setGroup(@PathVariable UUID userId, @RequestBody GroupStrategy groupStrategy) {
        User currentUser = userFacade.getAuthenticatedUser();

        // Checks if currentUser has permission to assign group to that user
        if (currentUser.hasPermission(Permission.ASSIGN_GROUP)) {
            userFacade.setUserGroup(userId, groupStrategy);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }
}
