package com.sadsky.sadsky.user;


import com.querydsl.core.types.Predicate;
import com.sadsky.sadsky.user.response.UserDetailsResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.UUID;

interface QueryUserControllerPort {
    PagedModel<EntityModel<UserDetailsResponse>> getUsers(Predicate predicate, String order, String sort, Integer size, Integer page);

    UserDetailsResponse getUser(UUID userId);
}
