package pl.skyterix.sadsky.user;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.skyterix.sadsky.exception.BlacklistedSortException;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.GroupUnauthorizedException;
import pl.skyterix.sadsky.pageable.PageableRequest;
import pl.skyterix.sadsky.prediction.QueryPredictionController;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserFacade;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.user.domain.group.Permission;
import pl.skyterix.sadsky.user.domain.group.SelfPermission;
import pl.skyterix.sadsky.user.response.UserDetailsResponse;
import pl.skyterix.sadsky.util.JpaModelMapper;
import pl.skyterix.sadsky.util.SortBlacklistUtil;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Skyte
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class QueryUserController implements QueryUserControllerPort {

    private final SortBlacklistUtil sortBlacklistUtil;
    private final UserFacade userFacade;
    private final JpaModelMapper jpaModelMapper;
    private final PagedResourcesAssembler<UserDetailsResponse> pagedResourcesAssembler;

    @Override
    @GetMapping
    public PagedModel<EntityModel<UserDetailsResponse>> getUsers(@QuerydslPredicate(root = User.class) Predicate predicate,
                                                                 @RequestParam(required = false) String order,
                                                                 @RequestParam(required = false) String sort,
                                                                 @RequestParam(required = false) Integer size,
                                                                 @RequestParam(required = false) Integer page) {
        // Build page from page informations
        Pageable pageable = PageableRequest.builder()
                .order(order)
                .sort(sort)
                .size(size)
                .page(page)
                .build().toPageable();

        // Make sure predicate is does not contain blocked fields
        if (sortBlacklistUtil.getBlackListedFields(User.class).contains(pageable.getSort().toString().strip())) {
            throw new BlacklistedSortException(Errors.SORT_NOT_ALLOWED_ON_FIELD.getErrorMessage(pageable.getSort().toString()));
        }

        User currentUser = userFacade.getAuthenticatedUser();

        Page<UserDTO> users;

        // Check if currentUser has permissions to get full users or reduced users
        if (currentUser.hasPermission(Permission.GET_FULL_USERS)) {
            users = userFacade.getFullUsers(predicate, pageable);
        } else if (currentUser.hasPermission(Permission.GET_MINI_USERS)) {
            users = userFacade.getMiniUsers(predicate, pageable);
        } else {
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
        }

        // Map to response and add hateoas
        Page<UserDetailsResponse> userDetailsResponses = users.stream()
                .map((userDTO) -> jpaModelMapper.mapEntity(userDTO, UserDetailsResponse.class))
                .map(this::addUserRelations)
                .collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> new PageImpl<>(list, pageable, users.getTotalElements())));

        return pagedResourcesAssembler.toModel(userDetailsResponses);
    }

    @Override
    @GetMapping("/{userId}")
    public UserDetailsResponse getUser(@PathVariable UUID userId) {
        User currentUser = userFacade.getAuthenticatedUser();

        UserDTO userDTO;

        // Check if currentUser has permissions to get full user or reduced user
        if (currentUser.hasPermission(userId, SelfPermission.GET_FULL_SELF_USER, Permission.GET_FULL_USER)) {
            userDTO = userFacade.getFullUser(userId);
        } else if (currentUser.hasPermission(userId, SelfPermission.GET_MINI_SELF_USER, Permission.GET_MINI_USER)) {
            userDTO = userFacade.getMiniUser(userId);
        } else {
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
        }

        // Add hateoas and map to response
        return addUserRelations(jpaModelMapper.mapEntity(userDTO, UserDetailsResponse.class));
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
