package pl.skyterix.sadsky.user.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MiniUserDTO {

    private long id;

    private UUID userId;

    private String firstName;

    private String lastName;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GroupStrategy group;

    private LocalDateTime createDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;
}
