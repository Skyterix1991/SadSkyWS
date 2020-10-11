package pl.skyterix.sadsky.prediction.domain.day.domain.dto;

import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DayDTO {

    private long id;

    private UUID dayId;

    private List<Emotion> morningEmotions;

    private List<Emotion> afternoonEmotions;

    private List<Emotion> eveningEmotions;

    private PredictionDTO prediction;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}
