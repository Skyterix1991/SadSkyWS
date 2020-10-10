package pl.skyterix.sadsky.user.domain.prediction.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import pl.skyterix.sadsky.user.response.UserDetailsResponse;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class PredictionDetailsResponse extends RepresentationModel<PredictionDetailsResponse> implements Serializable {

    private UUID predictionId;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserDetailsResponse owner;

    private LocalDateTime updateDate;

    private LocalDateTime createDate;

}