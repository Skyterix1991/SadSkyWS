package pl.skyterix.sadsky.prediction.domain.day.domain.dto;

import lombok.Data;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;

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

    private int dayNumber;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}
