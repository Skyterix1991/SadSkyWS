package pl.skyterix.sadsky.user.domain.prediction.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.user.domain.prediction.domain.dto.PredictionDTO;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.util.UUID;

@RequiredArgsConstructor
class CreatePredictionAdapter implements CreatePredictionPort {

    private final PredictionRepositoryPort predictionRepositoryAdapter;
    private final JpaModelMapper jpaModelMapper;

    @Override
    public UUID createPrediction(PredictionDTO predictionDTO) {
        Prediction prediction = predictionRepositoryAdapter.createPrediction(jpaModelMapper.mapEntity(predictionDTO, Prediction.class));

        return prediction.getPredictionId();
    }
}
