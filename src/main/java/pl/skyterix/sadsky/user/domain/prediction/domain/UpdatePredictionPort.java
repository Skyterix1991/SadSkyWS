package pl.skyterix.sadsky.user.domain.prediction.domain;

import pl.skyterix.sadsky.user.domain.prediction.domain.dto.PredictionDTO;

import java.util.UUID;

interface UpdatePredictionPort {
    void updatePrediction(UUID predictionId, PredictionDTO predictionDTO);
}
