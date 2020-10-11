package pl.skyterix.sadsky.user.domain.prediction.domain;

import java.util.UUID;

interface DeletePredictionPort {
    void deletePrediction(UUID predictionId);
}
