package com.sadsky.sadsky.user;

import com.sadsky.sadsky.user.domain.UserFacade;
import com.sadsky.sadsky.user.domain.dto.UserDTO;
import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;
import com.sadsky.sadsky.user.request.UserDetailsRequest;
import com.sadsky.sadsky.user.request.UserReplaceRequest;
import com.sadsky.sadsky.user.request.UserUpdateRequest;
import com.sadsky.sadsky.util.JpaModelMapper;
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
        userFacade.deleteUser(userId);
    }

    @Override
    @PatchMapping("/{userId}")
    public void updateUser(@PathVariable UUID userId, @RequestBody @Validated UserUpdateRequest userUpdateRequest) {
        UserDTO userDTO = jpaModelMapper.mapEntity(userUpdateRequest, UserDTO.class);

        userFacade.updateUser(userId, userDTO);
    }

    @Override
    @PutMapping("/{userId}")
    public void replaceUser(@PathVariable UUID userId, @RequestBody @Validated UserReplaceRequest userReplaceRequest) {
        UserDTO userDTO = jpaModelMapper.mapEntity(userReplaceRequest, UserDTO.class);

        userFacade.replaceUser(userId, userDTO);
    }

    @Override
    @PostMapping("/{userId}/group")
    public void setGroup(@PathVariable UUID userId, @RequestBody GroupStrategy groupStrategy) {
        userFacade.setUserGroup(userId, groupStrategy);
    }
}
