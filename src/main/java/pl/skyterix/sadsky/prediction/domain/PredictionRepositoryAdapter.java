package pl.skyterix.sadsky.prediction.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundException;

import javax.transaction.Transactional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class, dontRollbackOn = RuntimeException.class)
class PredictionRepositoryAdapter implements PredictionRepositoryPort {

    private final PredictionRepository predictionRepository;

    @Override
    public Prediction createPrediction(Prediction prediction) {
        return predictionRepository.save(prediction);
    }

    @Override
    public void updatePrediction(Prediction prediction) {
        predictionRepository.save(prediction);
    }

    @Override
    public void replacePrediction(Prediction prediction) {
        predictionRepository.save(prediction);
    }

    @Override
    public void deleteByPredictionId(UUID predictionId) {
        if (!predictionRepository.existsByPredictionId(predictionId)) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(predictionId.toString()));
        }

        predictionRepository.deleteByPredictionId(predictionId);
    }

    @Override
    public Prediction findByPredictionId(UUID predictionId) {
        return predictionRepository.findPredictionByPredictionId(predictionId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(predictionId.toString())));
    }

    @Override
    public boolean existsByPredictionId(UUID predictionId) {
        return predictionRepository.existsByPredictionId(predictionId);
    }

    @Override
    public Prediction findByUserIdAndPredictionId(UUID userId, UUID predictionId) {
        return predictionRepository.findPredictionByUserIdAndPredictionId(userId, predictionId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(predictionId.toString())));
    }

}