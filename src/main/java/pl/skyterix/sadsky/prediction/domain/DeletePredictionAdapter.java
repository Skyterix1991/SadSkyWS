package pl.skyterix.sadsky.prediction.domain;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
class DeletePredictionAdapter implements DeletePredictionPort {

    private final PredictionRepositoryPort predicamentRepositoryAdapter;

    @Override
    public void deletePrediction(UUID predictionId) {
        predicamentRepositoryAdapter.deleteByPredictionId(predictionId);
    }
}
