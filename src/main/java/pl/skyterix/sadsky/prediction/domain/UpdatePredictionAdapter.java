package pl.skyterix.sadsky.prediction.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;

import java.util.UUID;

@RequiredArgsConstructor
class UpdatePredictionAdapter implements UpdatePredictionPort {

    private final PredictionRepositoryPort predictionRepository;

    @Override
    public void updatePrediction(UUID predictionId, PredictionDTO predictionDTO) {
        Prediction prediction = predictionRepository.findByPredictionId(predictionId);

        predictionRepository.updatePrediction(prediction);
    }
}