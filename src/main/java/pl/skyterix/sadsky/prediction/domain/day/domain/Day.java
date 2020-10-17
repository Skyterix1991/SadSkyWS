package pl.skyterix.sadsky.prediction.domain.day.domain;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import pl.skyterix.sadsky.exception.DayDeadlineException;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.prediction.domain.Prediction;
import pl.skyterix.sadsky.util.annotation.SortBlacklisted;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity
@Data
@NoArgsConstructor
public class Day {

    // An integer of hours between each part of day ex. Wake hour is 7 so for 5 hours deadlines are 12 (morning), 17 (afternoon), 22 (noon)
    public final static int DAY_PART_HOURS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SortBlacklisted
    @QueryType(PropertyType.NONE)
    private long id;

    @NaturalId
    @Column(nullable = false, updatable = false)
    private UUID dayId;

    @Column(nullable = false, updatable = false)
    private int dayNumber;

    @Column
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Emotion.class)
    private Set<Emotion> morningEmotions;

    @Column
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Emotion.class)
    private Set<Emotion> afternoonEmotions;

    @Column
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Emotion.class)
    private Set<Emotion> eveningEmotions;

    @Column(updatable = false)
    private LocalDateTime createDate;

    @Column
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;

    public Day(int dayNumber) {
        this.morningEmotions = new HashSet<>();
        this.afternoonEmotions = new HashSet<>();
        this.eveningEmotions = new HashSet<>();

        this.dayNumber = dayNumber;
    }

    @PrePersist
    protected void onCreate() {
        this.dayId = UUID.randomUUID();
        this.createDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    public int countPoints(ToIntFunction<Emotion> toIntFunction) {
        int morningPoints = this.getMorningEmotions().stream()
                .mapToInt(toIntFunction)
                .sum();

        int afternoonPoints = this.getAfternoonEmotions().stream()
                .mapToInt(toIntFunction)
                .sum();

        int eveningPoints = this.getEveningEmotions().stream()
                .mapToInt(toIntFunction)
                .sum();

        return morningPoints + afternoonPoints + eveningPoints;
    }

    /**
     * Sets emotions for current part of the day or throws exception if not possible.
     *
     * @param prediction Prediction to base deadlines calculations on.
     * @param emotions   New emotions.
     */
    public void setEmotions(Prediction prediction, Set<Emotion> emotions) {
        // LocalDateTime list with deadlines for emotions fill
        List<LocalDateTime> deadlines = getDeadlines(prediction);

        // Morning emotions
        if (isInDeadline(deadlines.get(0), deadlines.get(1))) {
            this.setMorningEmotions(emotions);
            // Afternoon emotions
        } else if (isInDeadline(deadlines.get(1), deadlines.get(2))) {
            this.setAfternoonEmotions(emotions);
            // Evening emotions
        } else if (isInDeadline(deadlines.get(2), deadlines.get(3))) {
            this.setEveningEmotions(emotions);
            // Is it outside of any deadline
        } else {
            throw new DayDeadlineException(Errors.DAY_DEADLINE_EXCEPTION.getErrorMessage());
        }
    }

    private boolean isInDeadline(LocalDateTime start, LocalDateTime end) {
        LocalDateTime currentTime = LocalDateTime.now();

        // Is current time between start and end.
        return currentTime.isEqual(start) || currentTime.isAfter(start) && currentTime.isBefore(end);
    }

    private List<LocalDateTime> getDeadlines(Prediction prediction) {
        return IntStream.range(0, 4)
                .mapToObj(i -> {
                    // Create date time from expire date and subtract number of passed days.
                    LocalDateTime deadlineTime = prediction.getExpireDate().atStartOfDay().minusDays(Prediction.EXPIRE_DAYS - this.dayNumber + 1);
                    // Calculate deadline for current i.
                    return deadlineTime.plusHours(prediction.getOwner().getWakeHour() + Day.DAY_PART_HOURS * i);
                }).collect(Collectors.toUnmodifiableList());
    }
}