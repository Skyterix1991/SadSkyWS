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
import java.util.Set;
import java.util.UUID;
import java.util.function.ToIntFunction;
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
        this.dayNumber = dayNumber;
    }

    @PrePersist
    protected void onCreate() {
        if (morningEmotions == null) morningEmotions = new HashSet<>();
        if (afternoonEmotions == null) afternoonEmotions = new HashSet<>();
        if (eveningEmotions == null) eveningEmotions = new HashSet<>();

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
     * @param wakeHour User wake hour.
     * @param emotions New emotions.
     */
    public void setEmotions(int wakeHour, Set<Emotion> emotions) {
        int currentHour = LocalDateTime.now().getHour();

        // Hours of deadlines for emotions fill
        int[] deadlines = IntStream.range(1, 4)
                .map(i -> wakeHour + Day.DAY_PART_HOURS * i)
                .toArray();

        // Is it outside of any deadline
        if (wakeHour > currentHour && currentHour > deadlines[2])
            throw new DayDeadlineException(Errors.DAY_DEADLINE_EXCEPTION.getErrorMessage());

        // Morning emotions
        if (wakeHour <= currentHour && currentHour < deadlines[0]) {
            this.setMorningEmotions(emotions);
            // Afternoon emotions
        } else if (deadlines[1] <= currentHour && currentHour < deadlines[2]) {
            this.setAfternoonEmotions(emotions);
            // Evening emotions
        } else if (deadlines[2] <= currentHour && currentHour < deadlines[3]) {
            this.setEveningEmotions(emotions);
        }
    }
}