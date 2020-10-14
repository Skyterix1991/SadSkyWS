package pl.skyterix.sadsky.prediction.domain;

import java.util.UUID;

interface GeneratePredictionResultPort {
    void generatePredictionResult(UUID userId, UUID predictionId);
}
