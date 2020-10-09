package pl.skyterix.sadsky.user.domain.prediction.domain;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.skyterix.sadsky.user.domain.prediction.domain.dto.PredictionDTO;

import java.util.UUID;

interface PredictionFacadePort {
    Page<PredictionDTO> getPredictions(Predicate predicate, Pageable pageRequest);

    PredictionDTO getPrediction(UUID predictionId);

    void updatePrediction(UUID predictionId, PredictionDTO predictionDTO);

    void replacePrediction(UUID predictionId, PredictionDTO predictionDTO);

    UUID createPrediction(PredictionDTO predictionDTO);

    void deletePrediction(UUID predictionId);
}
