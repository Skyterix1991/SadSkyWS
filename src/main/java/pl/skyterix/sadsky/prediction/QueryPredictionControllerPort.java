package pl.skyterix.sadsky.prediction;


import pl.skyterix.sadsky.prediction.response.PredictionDetailsResponse;

import java.util.Set;
import java.util.UUID;

interface QueryPredictionControllerPort {
    Set<PredictionDetailsResponse> getUserPredictions(UUID userId);

    PredictionDetailsResponse getPrediction(UUID userId, UUID predictionId);
}
