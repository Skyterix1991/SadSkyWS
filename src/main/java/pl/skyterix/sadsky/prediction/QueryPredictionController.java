package pl.skyterix.sadsky.prediction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.GroupUnauthorizedException;
import pl.skyterix.sadsky.prediction.domain.PredictionFacade;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;
import pl.skyterix.sadsky.prediction.response.PredictionDetailsResponse;
import pl.skyterix.sadsky.user.QueryUserController;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserFacade;
import pl.skyterix.sadsky.user.domain.group.Permission;
import pl.skyterix.sadsky.user.domain.group.SelfPermission;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/predictions")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class QueryPredictionController implements QueryPredictionControllerPort {

    private final UserFacade userFacade;
    private final PredictionFacade predictionFacade;
    private final JpaModelMapper jpaModelMapper;

    @Override
    @GetMapping
    public Set<PredictionDetailsResponse> getUserPredictions(@PathVariable UUID userId) {

        User currentUser = userFacade.getAuthenticatedUser();

        User targetUser = jpaModelMapper.mapEntity(userFacade.getFullUser(userId), User.class);

        Set<PredictionDTO> predictions;

        // Check if currentUser has permissions to view user predictions
        if (currentUser.hasPermission(userId, SelfPermission.GET_SELF_USER_PREDICTIONS, Permission.GET_USER_PREDICTIONS))

            // Determine if user can fetch full or reduced version of user
            if (currentUser.hasPermission(userId, SelfPermission.GET_FULL_SELF_USER, Permission.GET_FULL_USER))
                predictions = predictionFacade.getFullUserPredictions(userId);
            else if (currentUser.hasPermission(userId, SelfPermission.GET_MINI_SELF_USER, Permission.GET_MINI_USER))
                predictions = predictionFacade.getMiniUserPredictions(userId);
            else
                throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

            // Check if currentUser is friends to target user
        else if (currentUser.getFriendsTo().contains(targetUser))

            if (currentUser.hasPermission(Permission.GET_FULL_USERS))
                predictions = predictionFacade.getFullUserPredictions(userId);
            else
                predictions = predictionFacade.getMiniUserPredictions(userId);

        else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

        // Map to response and add hateoas
        return predictions.stream()
                .map((predictionDTO) -> jpaModelMapper.mapEntity(predictionDTO, PredictionDetailsResponse.class))
                .map(this::addPredictionRelations)
                .collect(Collectors.toSet());
    }

    @Override
    @GetMapping("/{predictionId}")
    public PredictionDetailsResponse getPrediction(@PathVariable UUID userId, @PathVariable UUID predictionId) {
        User currentUser = userFacade.getAuthenticatedUser();

        User targetUser = jpaModelMapper.mapEntity(userFacade.getFullUser(userId), User.class);

        PredictionDTO predictionDTO;

        // Check if currentUser has permissions to get user prediction
        if (currentUser.hasPermission(userId, SelfPermission.GET_SELF_USER_PREDICTION, Permission.GET_USER_PREDICTION))

            // Determine if user can fetch full or reduced version of user
            if (currentUser.hasPermission(userId, SelfPermission.GET_FULL_SELF_USER, Permission.GET_FULL_USER))
                predictionDTO = predictionFacade.getFullUserPrediction(userId, predictionId);
            else if (currentUser.hasPermission(userId, SelfPermission.GET_MINI_SELF_USER, Permission.GET_MINI_USER))
                predictionDTO = predictionFacade.getMiniUserPrediction(userId, predictionId);
            else
                throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

            // Check if currentUser is friends to target user
        else if (currentUser.getFriendsTo().contains(targetUser))

            if (currentUser.hasPermission(Permission.GET_FULL_USERS))
                predictionDTO = predictionFacade.getFullUserPrediction(userId, predictionId);
            else
                predictionDTO = predictionFacade.getMiniUserPrediction(userId, predictionId);

        else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

        // Add hateoas and map to response
        return addPredictionRelations(jpaModelMapper.mapEntity(predictionDTO, PredictionDetailsResponse.class));
    }

    private PredictionDetailsResponse addPredictionRelations(PredictionDetailsResponse predictionDetailsResponse) {
        predictionDetailsResponse.add(linkTo(methodOn(QueryPredictionController.class).getPrediction(predictionDetailsResponse.getOwner().getUserId(), predictionDetailsResponse.getPredictionId())).withSelfRel());
        predictionDetailsResponse.add(linkTo(methodOn(QueryPredictionController.class).getUserPredictions(predictionDetailsResponse.getOwner().getUserId())).withRel("userPredictions"));
        predictionDetailsResponse.add(linkTo(methodOn(QueryUserController.class).getUser(predictionDetailsResponse.getOwner().getUserId())).withRel("user"));
        predictionDetailsResponse.add(linkTo(methodOn(QueryUserController.class).getUsers(null, null, null, null, null)).withRel("users"));

        return predictionDetailsResponse;
    }

}
