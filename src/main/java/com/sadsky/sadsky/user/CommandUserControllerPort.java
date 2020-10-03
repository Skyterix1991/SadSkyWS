package com.sadsky.sadsky.user;

import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;
import com.sadsky.sadsky.user.request.UserDetailsRequest;
import com.sadsky.sadsky.user.request.UserReplaceRequest;
import com.sadsky.sadsky.user.request.UserUpdateRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

interface CommandUserControllerPort {
    void createUser(UserDetailsRequest userDetailsRequest, HttpServletResponse response);

    void deleteUser(UUID userId);

    void updateUser(UUID userId, UserUpdateRequest userUpdateRequest);

    void replaceUser(UUID userId, UserReplaceRequest userReplaceRequest);

    void setGroup(UUID userId, GroupStrategy groupStrategy);
}
