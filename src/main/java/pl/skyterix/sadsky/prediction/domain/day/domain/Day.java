package pl.skyterix.sadsky.prediction.domain.day.domain;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.ToIntFunction;

@Entity
@Data
@NoArgsConstructor
public class Day {

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
    private List<Emotion> morningEmotions;

    @Column
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Emotion.class)
    private List<Emotion> afternoonEmotions;

    @Column
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Emotion.class)
    private List<Emotion> eveningEmotions;

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
        if (morningEmotions == null) morningEmotions = new ArrayList<>();
        if (afternoonEmotions == null) afternoonEmotions = new ArrayList<>();
        if (eveningEmotions == null) eveningEmotions = new ArrayList<>();

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
}