package pl.skyterix.sadsky.user.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDetailsResponse extends RepresentationModel<UserDetailsResponse> implements Serializable {

    private UUID userId;

    private String firstName;

    private String lastName;

    private LocalDate birthDay;

    private Integer wakeHour;

    private String email;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GroupStrategy group;

    private LocalDateTime lastTokenRevokeDate;

    private LocalDateTime updateDate;

    private LocalDateTime createDate;

}