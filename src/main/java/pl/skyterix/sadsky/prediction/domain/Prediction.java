package pl.skyterix.sadsky.prediction.domain;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.PredictionIsExpiredException;
import pl.skyterix.sadsky.prediction.domain.day.domain.Day;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.util.annotation.SortBlacklisted;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity
@Data
public class Prediction {

    private final static short EXPIRE_DAYS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SortBlacklisted
    @QueryType(PropertyType.NONE)
    private long id;

    @NaturalId
    @Column(nullable = false, updatable = false)
    private UUID predictionId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Day> days;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @QueryType(PropertyType.NONE)
    private User owner;

    @Column
    @Enumerated(EnumType.STRING)
    private DepressionResult depressionResult;

    @Column
    @Enumerated(EnumType.STRING)
    private AnxietyResult anxietyResult;

    @Column(nullable = false, updatable = false)
    private int expireDays;

    @Column(updatable = false)
    private LocalDate expireDate;

    @Column(updatable = false)
    private LocalDateTime createDate;

    @Column
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;

    public Prediction() {
        // Create each Day for expire day and assign its number in day constructor
        this.days = IntStream.range(1, EXPIRE_DAYS + 1)
                .mapToObj(Day::new)
                .collect(Collectors.toList());

        // Assign expire days in case of change of constant
        this.setExpireDays(EXPIRE_DAYS);
    }

    @PrePersist
    protected void onCreate() {
        if (days == null) days = new ArrayList<>();

        this.predictionId = UUID.randomUUID();

        LocalDateTime currentTime = LocalDateTime.now();

        this.expireDate = currentTime.toLocalDate().plusDays(EXPIRE_DAYS); // Expire in 7 days from now
        this.createDate = currentTime;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    public Day getCurrentDay() {
        return this.days.stream()
                .filter(day -> {
                    Period period = Period.between(LocalDate.now(), expireDate);

                    int currentDayNumber = (7 - period.getDays()) + 1; // Add one at the end cause day numerations starts from 1

                    // Is prediction expired
                    if (currentDayNumber > EXPIRE_DAYS)
                        throw new PredictionIsExpiredException(Errors.PREDICTION_IS_EXPIRED.getErrorMessage());

                    return day.getDayNumber() == currentDayNumber;
                })
                .findFirst().orElseThrow(() -> new IllegalStateException("Missing current day"));
    }
}
