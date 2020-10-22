package pl.skyterix.sadsky.prediction.domain.day.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Skyte
 */
@Data
@NoArgsConstructor
public class DayEmotionsRequest implements Serializable {

    @NotEmpty
    private Set<Emotion> emotions;

}
