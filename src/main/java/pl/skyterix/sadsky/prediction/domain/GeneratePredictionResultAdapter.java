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

    /**
     * Minimum emotion answers from all days to get result.
     */
    private final static int MIN_ANSWERS_REQUIRED = 7;

    @Override
    public void generatePredictionResult(UUID userId, UUID predictionId) {
        Prediction prediction = predictionRepositoryAdapter.findByUserIdAndPredictionId(userId, predictionId);

        User user = prediction.getOwner();

        // Is prediction already generated
        if (prediction.getDepressionResult() != null) {
            throw new PredictionResultIsAlreadyGeneratedException(Errors.PREDICTION_RESULT_IS_ALREADY_GENERATED.getErrorMessage());
        }

        // Check if prediction is not ready to generate results and throw exception if that's the case
        if (!isPredictionReady(user, prediction)) {
            throw new PredictionResultIsNotReadyToGenerateException(Errors.PREDICTION_RESULT_NOT_READY_TO_GENERATE.getErrorMessage());
        }

        // Check if predictions has enough answers to generate results if not cancel the prediction
        if (!hasPredictionRequiredAmountOfAnswers(prediction)) {
            prediction.setCanceled(true);

            predictionRepositoryAdapter.updatePrediction(prediction);

            generateNewPrediction(user);
            return;
        }

        // Count positive and negative points from emotions for each day and sum them
        int depressionPointsRatio = countPointsRatio(prediction, day -> day.countPointsRatio(Emotion::getDepressionPoints));
        int anxietyPointsRatio = countPointsRatio(prediction, day -> day.countPointsRatio(Emotion::getAnxietyPoints));

        // Count filled day parts count
        int totalAnswers = countFilledDayParts(prediction, Day::countFilledDayParts);

        // Run knn
        DepressionResult depressionResult = generateDepressionResult(depressionPointsRatio, totalAnswers);
        AnxietyResult anxietyResult = generateAnxietyResult(anxietyPointsRatio, totalAnswers);

        // Save prediction results
        prediction.setDepressionResult(depressionResult);
        prediction.setAnxietyResult(anxietyResult);

        predictionRepositoryAdapter.updatePrediction(prediction);

        generateNewPrediction(user);
    }

    private boolean hasPredictionRequiredAmountOfAnswers(Prediction prediction) {
        long predictionAnswers = prediction.getDays().stream()
                .mapToInt(day -> day.getTotalEmotionsAmount(day))
                .sum();

        return predictionAnswers >= MIN_ANSWERS_REQUIRED;
    }

    private void generateNewPrediction(User user) {
        // Create new prediction
        Prediction newPrediction = new Prediction();
        newPrediction.setOwner(user);
        newPrediction = predictionRepositoryAdapter.createPrediction(newPrediction);

        user.getPredictions().add(newPrediction);

        userRepository.save(user);
    }

    @SneakyThrows
    private Instances runKnnOnDataset(String datasetPath, int scoreRatio, int totalAnswers) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(datasetPath);

        assert inputStream != null;

        Instances data = new Instances(new InputStreamReader(inputStream));
        data.setClassIndex(data.numAttributes() - 1);

        LinearNNSearch knn = new LinearNNSearch(data);

        int k = (int) Math.sqrt(data.numInstances());
        // Check if even number if true add 1 to make it odd.
        k = (k % 2 == 0) ? k + 1 : k;

        Instance toPredictInstance = new DenseInstance(2);
        // Score ratio
        toPredictInstance.setValue(0, scoreRatio);
        // Total answers
        toPredictInstance.setValue(1, totalAnswers);

        return knn.kNearestNeighbours(toPredictInstance, k);
    }

    private AnxietyResult generateAnxietyResult(int anxietyScoreRatio, int totalAnswers) {
        Instances nearestInstances = runKnnOnDataset(
                ANXIETY_DATASET_PATH,
                anxietyScoreRatio,
                totalAnswers
        );

        long severePredictions = nearestInstances.stream()
                .map(instance -> instance.stringValue(2))
                .filter("SEVERE_ANXIETY"::equals)
                .count();

        long mildPredictions = nearestInstances.stream()
                .map(instance -> instance.stringValue(2))
                .filter("MILD_ANXIETY"::equals)
                .count();

        long negativePredictions = nearestInstances.numInstances() - severePredictions - mildPredictions;

        // If number of severe predictions is higher than mild predictions and higher than negative predictions
        if (severePredictions > mildPredictions && severePredictions > negativePredictions) {
            return AnxietyResult.SEVERE_ANXIETY;
        }
        // If number of mild predictions is higher than severe predictions and higher than negative predictions
        else if (mildPredictions > severePredictions && mildPredictions > negativePredictions) {
            return AnxietyResult.MILD_ANXIETY;
        } else {
            return AnxietyResult.NEGATIVE;
        }
    }

    private DepressionResult generateDepressionResult(int depressionScoreRatio, int totalAnswers) {
        // Run knn on depression dataset
        Instances nearestInstances = runKnnOnDataset(
                DEPRESSION_DATASET_PATH,
                depressionScoreRatio,
                totalAnswers
        );

        long severePredictions = nearestInstances.stream()
                .map(instance -> instance.stringValue(2))
                .filter("SEVERE_DEPRESSION"::equals)
                .count();

        long mildPredictions = nearestInstances.stream()
                .map(instance -> instance.stringValue(2))
                .filter("MILD_DEPRESSION"::equals)
                .count();

        long negativePredictions = nearestInstances.numInstances() - severePredictions - mildPredictions;

        // If number of severe predictions is higher than mild predictions and higher than negative predictions
        if (severePredictions > mildPredictions && severePredictions > negativePredictions) {
            return DepressionResult.SEVERE_DEPRESSION;
        }
        // If number of mild predictions is higher than severe predictions and higher than negative predictions
        else if (mildPredictions > severePredictions && mildPredictions > negativePredictions) {
            return DepressionResult.MILD_DEPRESSION;
        } else {
            return DepressionResult.NEGATIVE;
        }
    }

    private int countPointsRatio(Prediction prediction, ToIntFunction<Day> toIntFunction) {
        // Run toIntFunction interface on each day and sum the results
        return prediction.getDays().stream()
                .mapToInt(toIntFunction)
                .sum();
    }

    private int countFilledDayParts(Prediction prediction, ToIntFunction<Day> toIntFunction) {
        // Run toIntFunction interface on each day and sum the results
        return prediction.getDays().stream()
                .mapToInt(toIntFunction)
                .sum();
    }

    private boolean isPredictionReady(User user, Prediction prediction) {
        LocalDate currentTime = LocalDate.now();

        // Is prediction expired
        if (currentTime.isAfter(prediction.getExpireDate())) {
            return true;
        }

        int expireDays = prediction.getExpireDays();

        Day lastDay = prediction.getDays().stream()
                // Find last day in array
                .filter(day -> day.getDayNumber() == expireDays - 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing last day of prediction."));

        int currentHour = LocalDateTime.now().getHour();
        // Calculate last emotion deadline for last day
        int lastFillDeadline = user.getWakeHour() + Day.DAY_PART_HOURS * 3;

        // Is it a prediction expiration day
        if (currentTime.isEqual(prediction.getExpireDate()))
            // Are all possible emotions filled for last deadline or is currentHour greater/equal to last deadline hour
        {
            return !lastDay.getEveningEmotions().isEmpty() || currentHour >= lastFillDeadline;
        }

        return false;
    }

}
