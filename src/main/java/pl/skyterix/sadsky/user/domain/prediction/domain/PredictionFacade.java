package pl.skyterix.sadsky.user.domain.prediction.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.user.domain.prediction.domain.dto.MiniUserPredictionDTO;
import pl.skyterix.sadsky.user.domain.prediction.domain.dto.PredictionDTO;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PredictionFacade implements PredictionFacadePort {

    private final JpaModelMapper jpaModelMapper;
    private final PredictionRepository predictionRepository;
    private final CreatePredictionPort createPredictionAdapter;
    private final DeletePredictionPort deletePredictionAdapter;
    private final UpdatePredictionPort updatePredictionAdapter;
    private final ReplacePredictionPort replacePredictionAdapter;

    @Override
    public UUID createPrediction(PredictionDTO predictionDTO) {
        return createPredictionAdapter.createPrediction(predictionDTO);
    }

    @Override
    public void deletePrediction(UUID predictionId) {
        deletePredictionAdapter.deletePrediction(predictionId);
    }

    @Override
    public Set<PredictionDTO> getFullUserPredictions(UUID userId) {
        Set<Prediction> predictions = predictionRepository.findAllByUserId(userId);

        return predictions.stream()
                .map((prediction) -> jpaModelMapper.mapEntity(prediction, PredictionDTO.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PredictionDTO> getMiniUserPredictions(UUID userId) {
        Set<PredictionDTO> predictions = getFullUserPredictions(userId);

        return predictions.stream()
                .map((prediction) -> jpaModelMapper.mapEntity(prediction, MiniUserPredictionDTO.class))
                .map((prediction) -> jpaModelMapper.mapEntity(prediction, PredictionDTO.class))
                .collect(Collectors.toSet());
    }


    @Override
    public PredictionDTO getFullUserPrediction(UUID userId, UUID predictionId) {
        Prediction prediction = predictionRepository.findPredictionByUserIdAndPredictionId(userId, predictionId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(predictionId.toString())));

        return jpaModelMapper.mapEntity(prediction, PredictionDTO.class);
    }

    @Override
    public PredictionDTO getMiniUserPrediction(UUID userId, UUID predictionId) {
        PredictionDTO predictionDTO = getFullUserPrediction(userId, predictionId);

        // Map prediction to mini version and then back to full remove sensitive data

        MiniUserPredictionDTO miniUserPredictionDTO = jpaModelMapper.mapEntity(predictionDTO, MiniUserPredictionDTO.class);

        return jpaModelMapper.mapEntity(miniUserPredictionDTO, PredictionDTO.class);
    }

    @Override
    public void updatePrediction(UUID predictionId, PredictionDTO predictionDTO) {
        updatePredictionAdapter.updatePrediction(predictionId, predictionDTO);
    }

    @Override
    public void replacePrediction(UUID predictionId, PredictionDTO predictionDTO) {
        replacePredictionAdapter.replacePrediction(predictionId, predictionDTO);
    }

}
