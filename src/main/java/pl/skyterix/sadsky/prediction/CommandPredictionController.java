package pl.skyterix.sadsky.prediction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyterix.sadsky.prediction.domain.PredictionFacade;
import pl.skyterix.sadsky.user.domain.UserFacade;
import pl.skyterix.sadsky.util.JpaModelMapper;

@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommandPredictionController implements CommandPredictionControllerPort {

    private final UserFacade userFacade;
    private final PredictionFacade predictionFacade;
    private final JpaModelMapper jpaModelMapper;

}
