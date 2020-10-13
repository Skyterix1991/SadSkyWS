package pl.skyterix.sadsky.prediction.domain;

import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;

import java.util.Set;
import java.util.UUID;

interface PredictionFacadePort {
    Set<PredictionDTO> getFullUserPredictions(UUID userId);

    Set<PredictionDTO> getMiniUserPredictions(UUID userId);

    PredictionDTO getFullUserPrediction(UUID userId, UUID predictionId);

    PredictionDTO getMiniUserPrediction(UUID userId, UUID predictionId);

    void generatePredictionResult(UUID userId, UUID predictionId);

    void setPredictionDayEmotions(UUID userId, UUID predictionId, Set<Emotion> emotions);
}
