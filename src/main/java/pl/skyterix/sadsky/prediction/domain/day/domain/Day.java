package pl.skyterix.sadsky.prediction.domain.day.domain;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
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
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @QueryType(PropertyType.NONE)
    private Prediction prediction;

    @Column(updatable = false)
    private LocalDateTime createDate;

    @Column
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;

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
}