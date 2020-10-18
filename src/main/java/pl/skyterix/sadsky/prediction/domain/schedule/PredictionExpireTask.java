package pl.skyterix.sadsky.prediction.domain.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.skyterix.sadsky.prediction.domain.Prediction;
import pl.skyterix.sadsky.prediction.domain.PredictionFacade;
import pl.skyterix.sadsky.prediction.domain.PredictionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PredictionExpireTask {

    private final PredictionFacade predictionFacade;
    private final PredictionRepository predictionRepository;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void generateExpiredPredictionsResults() {
        List<Prediction> expiredPredictions = predictionRepository.findAllExpired(LocalDateTime.now());
        // Automatically generate results for expired predictions.
        expiredPredictions.forEach(prediction ->
                predictionFacade.generatePredictionResult(prediction.getOwner().getUserId(), prediction.getPredictionId()));
    }

}
