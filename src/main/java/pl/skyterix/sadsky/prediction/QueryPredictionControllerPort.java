package pl.skyterix.sadsky.prediction;


import pl.skyterix.sadsky.prediction.response.PredictionDetailsResponse;

import java.util.List;
import java.util.UUID;

interface QueryPredictionControllerPort {
    List<PredictionDetailsResponse> getUserPredictions(UUID userId);

    PredictionDetailsResponse getPrediction(UUID userId, UUID predictionId);
}
