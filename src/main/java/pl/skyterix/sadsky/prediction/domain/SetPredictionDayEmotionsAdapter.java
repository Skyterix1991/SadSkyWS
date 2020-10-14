package pl.skyterix.sadsky.prediction.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;
import pl.skyterix.sadsky.user.domain.UserRepository;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
class SetPredictionDayEmotionsAdapter implements SetPredictionDayEmotionsPort {

    private final PredictionRepositoryPort predicamentRepositoryAdapter;
    private final UserRepository userRepository;

    @Override
    public void setPredictionDayEmotions(UUID userId, UUID predictionId, Set<Emotion> emotions) {
        Prediction prediction = predicamentRepositoryAdapter.findByUserIdAndPredictionId(userId, predictionId);

        // Set emotions for current part of the day or throw exception
        prediction.getCurrentDay().setEmotions(prediction, emotions);

        predicamentRepositoryAdapter.updatePrediction(prediction);
    }
}
