package pl.skyterix.sadsky.prediction;

import pl.skyterix.sadsky.prediction.domain.day.request.DayEmotionsRequest;

import java.util.UUID;

interface CommandPredictionControllerPort {
    void setPredictionDayEmotions(UUID userId, UUID predictionId, DayEmotionsRequest dayEmotionsRequest);

    void generateResults(UUID userId, UUID predictionId);

}
