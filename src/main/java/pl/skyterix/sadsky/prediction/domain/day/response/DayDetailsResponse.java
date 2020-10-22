package pl.skyterix.sadsky.prediction.domain.day.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author Skyte
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DayDetailsResponse extends RepresentationModel<DayDetailsResponse> implements Serializable {

    private UUID dayId;

    private Set<Emotion> morningEmotions;

    private Set<Emotion> afternoonEmotions;

    private Set<Emotion> eveningEmotions;

    private int dayNumber;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

}