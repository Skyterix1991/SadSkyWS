package pl.skyterix.sadsky.prediction.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserRepository;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
class SetPredictionDayEmotionsAdapter implements SetPredictionDayEmotionsPort {

    private final PredictionRepositoryPort predicamentRepositoryAdapter;
    private final UserRepository userRepository;

    @Override
    public void setPredictionDayEmotions(UUID userId, UUID predictionId, Set<Emotion> emotions) {
        User user = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(userId.toString())));

        Prediction prediction = predicamentRepositoryAdapter.findByUserIdAndPredictionId(userId, predictionId);

        // Set emotions for current part of the day or throw exception
        prediction.getCurrentDay().setEmotions(user.getWakeHour(), emotions);

        predicamentRepositoryAdapter.updatePrediction(prediction);
    }
}
