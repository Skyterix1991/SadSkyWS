package pl.skyterix.sadsky.user.domain.prediction.domain;

import pl.skyterix.sadsky.user.domain.prediction.domain.dto.PredictionDTO;

import java.util.UUID;

interface ReplacePredictionPort {
    void replacePrediction(UUID predictionId, PredictionDTO predictionDTO);
}
