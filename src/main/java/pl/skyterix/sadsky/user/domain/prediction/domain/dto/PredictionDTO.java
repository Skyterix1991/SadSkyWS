package pl.skyterix.sadsky.user.domain.prediction.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PredictionDTO {

    private long id;

    private UUID predictionId;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserDTO owner;

    private LocalDateTime createDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;
}
