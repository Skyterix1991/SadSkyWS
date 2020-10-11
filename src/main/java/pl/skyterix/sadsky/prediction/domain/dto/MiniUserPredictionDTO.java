package pl.skyterix.sadsky.prediction.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.skyterix.sadsky.prediction.domain.AnxietyResult;
import pl.skyterix.sadsky.prediction.domain.DepressionResult;
import pl.skyterix.sadsky.user.domain.dto.MiniUserDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MiniUserPredictionDTO {

    private long id;

    private UUID predictionId;

    private DepressionResult depressionResult;

    private AnxietyResult anxietyResult;

    private LocalDate expireDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MiniUserDTO owner;

    private LocalDateTime createDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;
}
