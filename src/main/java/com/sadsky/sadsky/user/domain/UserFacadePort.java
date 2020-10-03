package com.sadsky.sadsky.user.domain;

import com.querydsl.core.types.Predicate;
import com.sadsky.sadsky.user.domain.dto.UserDTO;
import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

interface UserFacadePort {
    Page<UserDTO> getUsers(Predicate predicate, Pageable pageRequest);

    UserDTO getUser(UUID userId);

    void updateUser(UUID userId, UserDTO userDTO);

    void replaceUser(UUID userId, UserDTO userDTO);

    UUID createUser(UserDTO userDTO);

    void deleteUser(UUID userId);

    User getAuthenticatedUser();

    void setUserGroup(UUID userId, GroupStrategy groupStrategy);
}
