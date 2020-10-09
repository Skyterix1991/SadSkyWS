package pl.skyterix.sadsky.user.domain.prediction.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.user.domain.prediction.domain.dto.PredictionDTO;

import java.util.UUID;

@RequiredArgsConstructor
class ReplacePredictionAdapter implements ReplacePredictionPort {

    private final PredictionRepositoryPort predicateRepository;

    public void replacePrediction(UUID predictionId, PredictionDTO predicateDTO) {
        Prediction prediction = predicateRepository.findByPredictionId(predictionId);

        predicateRepository.replacePrediction(prediction);
    }
}
