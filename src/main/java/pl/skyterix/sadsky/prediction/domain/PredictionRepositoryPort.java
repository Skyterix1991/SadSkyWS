package pl.skyterix.sadsky.prediction.domain;

import java.util.UUID;

interface PredictionRepositoryPort {

    Prediction createPrediction(Prediction prediction);

    void updatePrediction(Prediction prediction);

    void replacePrediction(Prediction prediction);

    void deleteByPredictionId(UUID predictionId);

    Prediction findByPredictionId(UUID predictionId);

    boolean existsByPredictionId(UUID predictionId);
}
