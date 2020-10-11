package pl.skyterix.sadsky.prediction.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.prediction.domain.dto.MiniUserPredictionDTO;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;
import pl.skyterix.sadsky.user.domain.UserRepository;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PredictionFacade implements PredictionFacadePort {

    private final JpaModelMapper jpaModelMapper;
    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final CreatePredictionPort createPredictionAdapter;
    private final DeletePredictionPort deletePredictionAdapter;
    private final UpdatePredictionPort updatePredictionAdapter;
    private final ReplacePredictionPort replacePredictionAdapter;

    /**
     * Creates prediction based on input given in PredictionDTO validated before by request validators.
     *
     * @param predictionDTO Prediction to create.
     * @return Created prediction UUID.
     */
    @Override
    public UUID createPrediction(PredictionDTO predictionDTO) {
        return createPredictionAdapter.createPrediction(predictionDTO);
    }

    /**
     * Deletes prediction by predictionId.
     *
     * @param predictionId Prediction UUID.
     */
    @Override
    public void deletePrediction(UUID predictionId) {
        deletePredictionAdapter.deletePrediction(predictionId);
    }

    /**
     * Get user predictions in Set with full user details.
     *
     * @param userId User UUID whose predictions to return.
     * @return Result set.
     */
    @Override
    public Set<PredictionDTO> getFullUserPredictions(UUID userId) {
        if (!userRepository.existsByUserId(userId))
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(userId));

        Set<Prediction> predictions = predictionRepository.findAllByUserId(userId);

        return predictions.stream()
                .map((prediction) -> jpaModelMapper.mapEntity(prediction, PredictionDTO.class))
                .collect(Collectors.toSet());
    }

    /**
     * Get user predictions in Set with reduced user details.
     *
     * @param userId User UUID whose predictions to return.
     * @return Result set.
     */
    @Override
    public Set<PredictionDTO> getMiniUserPredictions(UUID userId) {
        Set<PredictionDTO> predictions = getFullUserPredictions(userId);

        return predictions.stream()
                .map((prediction) -> jpaModelMapper.mapEntity(prediction, MiniUserPredictionDTO.class))
                .map((prediction) -> jpaModelMapper.mapEntity(prediction, PredictionDTO.class))
                .collect(Collectors.toSet());
    }

    /**
     * Get prediction by predictionId with full details.
     *
     * @param userId       User UUID whose predictions to return.
     * @param predictionId Prediction UUID to search for.
     * @return Prediction with given UUID.
     */
    @Override
    public PredictionDTO getFullUserPrediction(UUID userId, UUID predictionId) {
        Prediction prediction = predictionRepository.findPredictionByUserIdAndPredictionId(userId, predictionId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(predictionId.toString())));

        return jpaModelMapper.mapEntity(prediction, PredictionDTO.class);
    }

    /**
     * Get prediction by predictionId with reduced details.
     *
     * @param userId       User UUID whose predictions to return.
     * @param predictionId Prediction UUID to search for.
     * @return Prediction with given UUID.
     */
    @Override
    public PredictionDTO getMiniUserPrediction(UUID userId, UUID predictionId) {
        PredictionDTO predictionDTO = getFullUserPrediction(userId, predictionId);

        // Map prediction to mini version and then back to full remove sensitive data

        MiniUserPredictionDTO miniUserPredictionDTO = jpaModelMapper.mapEntity(predictionDTO, MiniUserPredictionDTO.class);

        return jpaModelMapper.mapEntity(miniUserPredictionDTO, PredictionDTO.class);
    }

    /**
     * Updates prediction by predictionId based on input given in PredictionDTO validated before by request validators.
     * Update will only be performed on field if its value is different than null.
     *
     * @param predictionId  Prediction UUID.
     * @param predictionDTO Updated prediction.
     */
    @Override
    public void updatePrediction(UUID predictionId, PredictionDTO predictionDTO) {
        updatePredictionAdapter.updatePrediction(predictionId, predictionDTO);
    }

    /**
     * Replaces prediction by predictionId based on input given in PredictionDTO validated before by request validators.
     * All fields will be overwritten.
     *
     * @param predictionId  Prediction UUID.
     * @param predictionDTO Replaced prediction.
     */
    @Override
    public void replacePrediction(UUID predictionId, PredictionDTO predictionDTO) {
        replacePredictionAdapter.replacePrediction(predictionId, predictionDTO);
    }

}
