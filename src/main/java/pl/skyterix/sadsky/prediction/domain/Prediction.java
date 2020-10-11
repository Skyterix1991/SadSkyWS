package pl.skyterix.sadsky.prediction.domain;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import pl.skyterix.sadsky.prediction.domain.day.domain.Day;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.util.annotation.SortBlacklisted;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SortBlacklisted
    @QueryType(PropertyType.NONE)
    private long id;

    @NaturalId
    @Column(nullable = false, updatable = false)
    private UUID predictionId;

    @OneToMany(mappedBy = "prediction", cascade = CascadeType.ALL)
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

    @Column(updatable = false)
    private LocalDate expireDate;

    @Column(updatable = false)
    private LocalDateTime createDate;

    @Column
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;

    @PrePersist
    protected void onCreate() {
        if (days == null) days = new ArrayList<>();

        this.predictionId = UUID.randomUUID();

        LocalDateTime currentTime = LocalDateTime.now();

        this.expireDate = currentTime.toLocalDate().plusDays(7); // Expire in 7 days from now
        this.createDate = currentTime;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}
