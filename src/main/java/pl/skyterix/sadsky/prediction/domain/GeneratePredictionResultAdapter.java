package pl.skyterix.sadsky.prediction.domain;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.PredictionResultIsAlreadyGeneratedException;
import pl.skyterix.sadsky.exception.PredictionResultIsNotReadyToGenerateException;
import pl.skyterix.sadsky.prediction.domain.day.domain.Day;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserRepository;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.ToIntFunction;

@RequiredArgsConstructor
class GeneratePredictionResultAdapter implements GeneratePredictionResultPort {

    private final PredictionRepositoryPort predictionRepositoryAdapter;
    private final UserRepository userRepository;

    private final static String DEPRESSION_DATASET_PATH = "datasets/depression.arff";
    private final static String ANXIETY_DATASET_PATH = "datasets/anxiety.arff";

    @Override
    public void generatePredictionResult(UUID userId, UUID predictionId) {
        Prediction prediction = predictionRepositoryAdapter.findByUserIdAndPredictionId(userId, predictionId);

        User user = prediction.getOwner();

        // Is prediction already generated
        if (prediction.getDepressionResult() != null)
            throw new PredictionResultIsAlreadyGeneratedException(Errors.PREDICTION_RESULT_IS_ALREADY_GENERATED.getErrorMessage());

        // Check if prediction is not ready to generate results and throw exception if that's the case
        if (!isPredictionReady(user, prediction))
            throw new PredictionResultIsNotReadyToGenerateException(Errors.PREDICTION_RESULT_NOT_READY_TO_GENERATE.getErrorMessage());

        // Count positive and negative points from emotions for each day and sum them
        int positiveDepressionPoints = countPoints(prediction, day -> day.countPoints(Emotion::getDepressionPositivePoints));
        int negativeDepressionPoints = countPoints(prediction, day -> day.countPoints(Emotion::getDepressionNegativePoints));

        int positiveAnxietyPoints = countPoints(prediction, day -> day.countPoints(Emotion::getAnxietyPositivePoints));
        int negativeAnxietyPoints = countPoints(prediction, day -> day.countPoints(Emotion::getAnxietyNegativePoints));

        // Run knn
        DepressionResult depressionResult = generateDepressionResult(positiveDepressionPoints, negativeDepressionPoints);
        AnxietyResult anxietyResult = generateAnxietyResult(positiveAnxietyPoints, negativeAnxietyPoints);

        // Save prediction results
        prediction.setDepressionResult(depressionResult);
        prediction.setAnxietyResult(anxietyResult);

        predictionRepositoryAdapter.updatePrediction(prediction);

        // Create new prediction
        user.getPredictions().add(new Prediction());

        userRepository.save(user);
    }

    @SneakyThrows
    private Instances runKnnOnDataset(String datasetPath, int positiveScore, int negativeScore) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(datasetPath);

        assert inputStream != null;

        Instances data = new Instances(new InputStreamReader(inputStream));
        data.setClassIndex(data.numAttributes() - 1);

        LinearNNSearch knn = new LinearNNSearch(data);

        int k = (int) Math.sqrt(data.numInstances());
        // Check if even number if true add 1 to make it odd.
        k = (k % 2 == 0) ? k + 1 : k;

        Instance toPredictInstance = new DenseInstance(2);
        toPredictInstance.setValue(0, positiveScore); // Positive score
        toPredictInstance.setValue(1, negativeScore); // Negative score

        return knn.kNearestNeighbours(toPredictInstance, k);
    }

    private AnxietyResult generateAnxietyResult(int positiveAnxietyScore, int negativeAnxietyScore) {
        Instances nearestInstances = runKnnOnDataset(
                ANXIETY_DATASET_PATH,
                positiveAnxietyScore,
                negativeAnxietyScore
        );

        long severePredictions = nearestInstances.stream()
                .map(instance -> instance.stringValue(2))
                .filter(predicate -> predicate.equals("SEVERE_ANXIETY"))
                .count();

        long mildPredictions = nearestInstances.stream()
                .map(instance -> instance.stringValue(2))
                .filter(predicate -> predicate.equals("MILD_ANXIETY"))
                .count();

        // If number of severe predictions is higher than mild predictions and higher than negative predictions
        if (severePredictions > mildPredictions && severePredictions > nearestInstances.numInstances())
            return AnxietyResult.SEVERE_ANXIETY;
            // If number of mild predictions is higher than severe predictions and higher than negative predictions
        else if (mildPredictions > severePredictions && mildPredictions > nearestInstances.numInstances())
            return AnxietyResult.MILD_ANXIETY;
        else
            return AnxietyResult.NEGATIVE;
    }

    private DepressionResult generateDepressionResult(int positiveDepressionScore, int negativeDepressionScore) {
        // Run knn on depression dataset
        Instances nearestInstances = runKnnOnDataset(
                DEPRESSION_DATASET_PATH,
                positiveDepressionScore,
                negativeDepressionScore
        );

        long severePredictions = nearestInstances.stream()
                .map(instance -> instance.stringValue(2))
                .filter(predicate -> predicate.equals("SEVERE_DEPRESSION"))
                .count();

        long mildPredictions = nearestInstances.stream()
                .map(instance -> instance.stringValue(2))
                .filter(predicate -> predicate.equals("MILD_DEPRESSION"))
                .count();

        // If number of severe predictions is higher than mild predictions and higher than negative predictions
        if (severePredictions > mildPredictions && severePredictions > nearestInstances.numInstances())
            return DepressionResult.SEVERE_DEPRESSION;
            // If number of mild predictions is higher than severe predictions and higher than negative predictions
        else if (mildPredictions > severePredictions && mildPredictions > nearestInstances.numInstances())
            return DepressionResult.MILD_DEPRESSION;
        else
            return DepressionResult.NEGATIVE;
    }

    private int countPoints(Prediction prediction, ToIntFunction<Day> toIntFunction) {
        // Run toIntFunction interface on each day and sum the results
        return prediction.getDays().stream()
                .mapToInt(toIntFunction)
                .sum();
    }

    private boolean isPredictionReady(User user, Prediction prediction) {
        LocalDate currentTime = LocalDate.now();

        // Is prediction expired
        if (currentTime.isAfter(prediction.getExpireDate()))
            return true;

        int expireDays = prediction.getExpireDays();

        Day lastDay = prediction.getDays().stream()
                .filter(day -> day.getDayNumber() == expireDays - 1) // Find last day in array
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing last day of prediction."));

        int currentHour = LocalDateTime.now().getHour();
        // Calculate last emotion deadline for last day
        int lastFillDeadline = user.getWakeHour() + Day.DAY_PART_HOURS * 3;

        // Is it a prediction expiration day
        if (currentTime.isEqual(prediction.getExpireDate()))
            // Are all possible emotions filled for last deadline or is currentHour greater/equal to last deadline hour
            return !lastDay.getEveningEmotions().isEmpty() || currentHour >= lastFillDeadline;

        return false;
    }

}
