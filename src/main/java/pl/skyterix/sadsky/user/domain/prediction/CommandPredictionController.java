package pl.skyterix.sadsky.user.domain.prediction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyterix.sadsky.user.domain.UserFacade;
import pl.skyterix.sadsky.user.domain.prediction.domain.PredictionFacade;
import pl.skyterix.sadsky.util.JpaModelMapper;

@RestController
@RequestMapping("/users/{userId}/predictions")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommandPredictionController implements CommandPredictionControllerPort {

    private final UserFacade userFacade;
    private final PredictionFacade predictionFacade;
    private final JpaModelMapper jpaModelMapper;

}
