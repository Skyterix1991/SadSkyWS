package pl.skyterix.sadsky.user.domain.prediction.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MiniPredictionDTO {

    private long id;

    private UUID predictionId;

    private LocalDateTime createDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;
}
