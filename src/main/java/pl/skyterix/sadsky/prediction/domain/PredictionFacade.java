package pl.skyterix.sadsky.prediction.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;
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
    private final GeneratePredictionResultPort generatePredictionResultAdapter;
    private final SetPredictionDayEmotionsPort setPredictionDayEmotionsAdapter;

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
     * Generate prediction result using knn algorithm.
     *
     * @param userId       Owner of prediction UUID.
     * @param predictionId Prediction UUID to generate results from.
     */
    @Override
    public void generatePredictionResult(UUID userId, UUID predictionId) {
        generatePredictionResultAdapter.generatePredictionResult(userId, predictionId);
    }

    /**
     * Sets emotions for current day in current prediction.
     * It will check for day deadlines and assign emotions to current one or throw exception.
     *
     * @param userId       Owner of prediction UUID.
     * @param predictionId Prediction UUID to generate results from.
     */
    @Override
    public void setPredictionDayEmotions(UUID userId, UUID predictionId, Set<Emotion> emotions) {
        setPredictionDayEmotionsAdapter.setPredictionDayEmotions(userId, predictionId, emotions);
    }

}
