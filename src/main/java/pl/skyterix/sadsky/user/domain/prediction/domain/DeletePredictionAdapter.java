package pl.skyterix.sadsky.user.domain.prediction.domain;

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
