package pl.skyterix.sadsky.prediction.domain;

import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;

import java.util.UUID;

interface ReplacePredictionPort {
    void replacePrediction(UUID predictionId, PredictionDTO predictionDTO);
}
