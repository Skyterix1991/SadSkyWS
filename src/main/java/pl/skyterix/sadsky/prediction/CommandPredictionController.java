package pl.skyterix.sadsky.prediction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.GroupUnauthorizedException;
import pl.skyterix.sadsky.prediction.domain.PredictionFacade;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserFacade;
import pl.skyterix.sadsky.user.domain.group.Permission;
import pl.skyterix.sadsky.user.domain.group.SelfPermission;

import java.util.UUID;

@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommandPredictionController implements CommandPredictionControllerPort {

    private final UserFacade userFacade;
    private final PredictionFacade predictionFacade;

    @Override
    @PostMapping("/{predictionId}/result/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public void generateResults(UUID userId, UUID predictionId) {
        User currentUser = userFacade.getAuthenticatedUser();

        // Checks if currentUser has permission to generate prediction for that user
        if (currentUser.hasPermission(userId, SelfPermission.GENERATE_SELF_PREDICTION_RESULT, Permission.GENERATE_PREDICTION_RESULT)) {
            predictionFacade.generatePredictionResult(userId, predictionId);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }
}
