package pl.skyterix.sadsky.user.domain.prediction;


import pl.skyterix.sadsky.user.domain.prediction.response.PredictionDetailsResponse;

import java.util.Set;
import java.util.UUID;

interface QueryPredictionControllerPort {
    Set<PredictionDetailsResponse> getUserPredictions(UUID userId);

    PredictionDetailsResponse getPrediction(UUID userId, UUID predictionId);
}
