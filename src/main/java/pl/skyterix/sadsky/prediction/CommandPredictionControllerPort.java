package pl.skyterix.sadsky.prediction;

import java.util.UUID;

interface CommandPredictionControllerPort {

    void generateResults(UUID userId, UUID predictionId);

}
