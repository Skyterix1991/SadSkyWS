package pl.skyterix.sadsky.prediction.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import pl.skyterix.sadsky.prediction.domain.AnxietyResult;
import pl.skyterix.sadsky.prediction.domain.DepressionResult;
import pl.skyterix.sadsky.prediction.domain.day.response.DayDetailsResponse;
import pl.skyterix.sadsky.user.response.UserDetailsResponse;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class PredictionDetailsResponse extends RepresentationModel<PredictionDetailsResponse> implements Serializable {

    private UUID predictionId;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserDetailsResponse owner;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<DayDetailsResponse> days;

    private DepressionResult depressionResult;

    private AnxietyResult anxietyResult;

    private LocalDate expireDate;

    private LocalDateTime updateDate;

    private LocalDateTime createDate;

}