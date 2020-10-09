package pl.skyterix.sadsky.user.domain.prediction.domain;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.GroupUnauthorizedException;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserFacade;
import pl.skyterix.sadsky.user.domain.group.Permission;
import pl.skyterix.sadsky.user.domain.group.SelfPermission;
import pl.skyterix.sadsky.user.domain.prediction.domain.dto.PredictionDTO;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PredictionFacade implements PredictionFacadePort {

    private final UserFacade userFacade;
    private final JpaModelMapper jpaModelMapper;
    private final PredictionRepository predictionRepository;
    private final CreatePredictionPort createPredictionAdapter;
    private final DeletePredictionPort deletePredictionAdapter;
    private final UpdatePredictionPort updatePredictionAdapter;
    private final ReplacePredictionPort replacePredictionAdapter;

    /**
     * Creates prediction.
     *
     * @param predictionDTO Prediction to create.
     * @return Created prediction UUID.
     */
    @Override
    public UUID createPrediction(PredictionDTO predictionDTO) {
        return createPredictionAdapter.createPrediction(predictionDTO);
    }

    /**
     * Deletes prediction.
     *
     * @param predictionId Prediction UUID.
     */
    @Override
    public void deletePrediction(UUID predictionId) {
        User currentUser = userFacade.getAuthenticatedUser();

        if (currentUser.hasPermission(predictionId, SelfPermission.DELETE_SELF_USER, Permission.DELETE_USER)) {
            deletePredictionAdapter.deletePrediction(predictionId);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

    /**
     * Get predictions in Page.
     *
     * @param predicate   Predicate to search with.
     * @param pageRequest Page info.
     * @return Paged result list.
     */
    @Override
    public Page<PredictionDTO> getPredictions(Predicate predicate, Pageable pageRequest) {
        User currentUser = userFacade.getAuthenticatedUser();

        Page<Prediction> predictions;

        if (currentUser.hasPermission(Permission.GET_FULL_USERS)) {
            predictions = predictionRepository.findAll(predicate, pageRequest);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

        return predictions.stream()
                .map((user) -> jpaModelMapper.mapEntity(user, PredictionDTO.class))
                .collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> new PageImpl<>(list, pageRequest, predictions.getTotalElements())));
    }

    /**
     * Get prediction.
     *
     * @param predictionId Prediction UUID.
     * @return Prediction with given UUID.
     */
    @Override
    public PredictionDTO getPrediction(UUID predictionId) {
        User currentUser = userFacade.getAuthenticatedUser();

        Prediction prediction;

        if (currentUser.hasPermission(predictionId, SelfPermission.GET_FULL_SELF_USER, Permission.GET_FULL_USER)) {

            prediction = predictionRepository.findPredictionByPredictionId(predictionId)
                    .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(predictionId.toString())));

        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

        return jpaModelMapper.mapEntity(prediction, PredictionDTO.class);
    }

    /**
     * Updates prediction.
     *
     * @param predictionId  Prediction UUID.
     * @param predictionDTO Updated prediction.
     */
    @Override
    public void updatePrediction(UUID predictionId, PredictionDTO predictionDTO) {
        User currentUser = userFacade.getAuthenticatedUser();

        if (currentUser.hasPermission(predictionId, SelfPermission.UPDATE_SELF_USER, Permission.UPDATE_USER)) {
            updatePredictionAdapter.updateUser(predictionId, predictionDTO);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

    /**
     * Replaces prediction.
     *
     * @param predictionId  Prediction UUID
     * @param predictionDTO Replacement prediction.
     */
    @Override
    public void replacePrediction(UUID predictionId, PredictionDTO predictionDTO) {
        User currentUser = userFacade.getAuthenticatedUser();

        if (currentUser.hasPermission(predictionId, SelfPermission.REPLACE_SELF_USER, Permission.REPLACE_USER)) {
            replacePredictionAdapter.replacePrediction(predictionId, predictionDTO);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

}
