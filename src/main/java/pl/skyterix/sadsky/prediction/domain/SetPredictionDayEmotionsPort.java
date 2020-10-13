package pl.skyterix.sadsky.prediction.domain;

import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;

import java.util.Set;
import java.util.UUID;

interface SetPredictionDayEmotionsPort {

    void setPredictionDayEmotions(UUID userId, UUID predictionId, Set<Emotion> emotions);

}
