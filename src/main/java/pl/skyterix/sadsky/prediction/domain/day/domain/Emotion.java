package pl.skyterix.sadsky.prediction.domain.day.domain;

import lombok.Getter;

public enum Emotion {

    /*
        Emotions points are counted as follows.
        Each emotion has three options: positive, neutral, negative.
        > 0 - Positive impact
        < 0 points - Negative impact
        == 0 points - Neutral impact

        You can set priority of each emotion by assigning it a lower or higher number ex. ACTIVE(-3, 0);
     */

    // Positive emotions
    ACTIVE(-1, 0),
    AMUSED(-1, 0),
    ENERGIZED(-1, 0),
    EXCITED(-1, 0),
    ENTHUSIASTIC(-1, -1),
    HAPPY(-1, 0),
    INTERESTED(-1, 0),
    PROUD(-1, 0),
    PEACEFUL(1, -1),
    FULFILLED(-1, -1),
    SAFE(0, -1),
    CONFIDENT(-1, -1),
    RELAXED(0, -1),

    // Negative emotions
    NERVOUS(0, 1),
    SCARED(1, 1),
    APATHETIC(1, 0),
    ASHAMED(1, 1),
    GUILTY(1, 1),
    HATE(1, 0),
    BRUNT(1, 0),
    DISHEARTENED(1, 0),
    HOLLOW(1, 0),
    HELPLESS(1, 1),
    HOPELESS(1, 0),
    LONELY(1, 0),
    SAD(1, 0),
    TIRED(1, 0),
    EXHAUSTED(1, 0);

    @Getter
    private final int depressionPoints;

    @Getter
    private final int anxietyPoints;

    Emotion(int depressionPoints, int anxietyPoints) {
        this.depressionPoints = depressionPoints;
        this.anxietyPoints = anxietyPoints;
    }

    public int getDepressionPositivePoints() {
        // Return only if points are positive if not return neutral 0
        return (depressionPoints > 0) ? depressionPoints : 0;
    }

    public int getDepressionNegativePoints() {
        // Return only if points are negative if not return neutral 0
        return (depressionPoints < 0) ? depressionPoints : 0;
    }

    public int getAnxietyPositivePoints() {
        // Return only if points are positive if not return neutral 0
        return (anxietyPoints > 0) ? anxietyPoints : 0;
    }

    public int getAnxietyNegativePoints() {
        // Return only if points are negative if not return neutral 0
        return (anxietyPoints < 0) ? anxietyPoints : 0;
    }
}
