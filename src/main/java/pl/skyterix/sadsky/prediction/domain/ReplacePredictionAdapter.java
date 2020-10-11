package pl.skyterix.sadsky.prediction.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;

import java.util.UUID;

@RequiredArgsConstructor
class ReplacePredictionAdapter implements ReplacePredictionPort {

    private final PredictionRepositoryPort predicateRepository;

    public void replacePrediction(UUID predictionId, PredictionDTO predicateDTO) {
        Prediction prediction = predicateRepository.findByPredictionId(predictionId);

        predicateRepository.replacePrediction(prediction);
    }
}
