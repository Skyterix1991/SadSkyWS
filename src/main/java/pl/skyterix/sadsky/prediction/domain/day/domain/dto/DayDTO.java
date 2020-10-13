package pl.skyterix.sadsky.prediction.domain.day.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class DayDTO {

    private long id;

    private UUID dayId;

    private Set<Emotion> morningEmotions;

    private Set<Emotion> afternoonEmotions;

    private Set<Emotion> eveningEmotions;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PredictionDTO prediction;

    private int dayNumber;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}
