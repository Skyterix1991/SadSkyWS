package pl.skyterix.sadsky.prediction.domain;

import java.util.UUID;

interface DeletePredictionPort {
    void deletePrediction(UUID predictionId);
}
