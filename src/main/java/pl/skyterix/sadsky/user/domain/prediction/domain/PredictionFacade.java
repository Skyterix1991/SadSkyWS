package pl.skyterix.sadsky.user.domain.prediction.domain;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.user.domain.UserFacade;
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

    @Override
    public UUID createPrediction(PredictionDTO predictionDTO) {
        return createPredictionAdapter.createPrediction(predictionDTO);
    }

    @Override
    public void deletePrediction(UUID predictionId) {
        deletePredictionAdapter.deletePrediction(predictionId);
    }

    @Override
    public Page<PredictionDTO> getPredictions(Predicate predicate, Pageable pageRequest) {
        Page<Prediction> predictions = predictionRepository.findAll(predicate, pageRequest);

        return predictions.stream()
                .map((prediction) -> jpaModelMapper.mapEntity(prediction, PredictionDTO.class))
                .collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> new PageImpl<>(list, pageRequest, predictions.getTotalElements())));
    }

    @Override
    public PredictionDTO getPrediction(UUID predictionId) {
        Prediction prediction = predictionRepository.findPredictionByPredictionId(predictionId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(predictionId.toString())));

        return jpaModelMapper.mapEntity(prediction, PredictionDTO.class);
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
