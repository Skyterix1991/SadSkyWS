package pl.skyterix.sadsky.prediction.domain;

import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;

import java.util.UUID;

interface UpdatePredictionPort {
    void updatePrediction(UUID predictionId, PredictionDTO predictionDTO);
}
