package pl.skyterix.sadsky.user;


import com.querydsl.core.types.Predicate;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import pl.skyterix.sadsky.user.response.UserDetailsResponse;

import java.util.UUID;

interface QueryUserControllerPort {
    PagedModel<EntityModel<UserDetailsResponse>> getUsers(Predicate predicate, String order, String sort, Integer size, Integer page);

    UserDetailsResponse getUser(UUID userId);
}
