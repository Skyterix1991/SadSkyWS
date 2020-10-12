package pl.skyterix.sadsky.prediction.domain.day.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class DayDTO {

    private long id;

    private UUID dayId;

    private List<Emotion> morningEmotions;

    private List<Emotion> afternoonEmotions;

    private List<Emotion> eveningEmotions;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PredictionDTO prediction;

    private int dayNumber;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}
