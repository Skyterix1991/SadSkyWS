package pl.skyterix.sadsky.user.domain.prediction.domain;

import pl.skyterix.sadsky.user.domain.prediction.domain.dto.PredictionDTO;

import java.util.Set;
import java.util.UUID;

interface PredictionFacadePort {
    Set<PredictionDTO> getFullUserPredictions(UUID userId);

    Set<PredictionDTO> getMiniUserPredictions(UUID userId);

    PredictionDTO getFullUserPrediction(UUID userId, UUID predictionId);

    PredictionDTO getMiniUserPrediction(UUID userId, UUID predictionId);

    void updatePrediction(UUID predictionId, PredictionDTO predictionDTO);

    void replacePrediction(UUID predictionId, PredictionDTO predictionDTO);

    UUID createPrediction(PredictionDTO predictionDTO);

    void deletePrediction(UUID predictionId);
}
