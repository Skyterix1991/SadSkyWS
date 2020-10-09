package pl.skyterix.sadsky.user.domain.prediction.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class PredictionDetailsResponse extends RepresentationModel<PredictionDetailsResponse> implements Serializable {

    private UUID predictionId;

    private LocalDateTime updateDate;

    private LocalDateTime createDate;

}