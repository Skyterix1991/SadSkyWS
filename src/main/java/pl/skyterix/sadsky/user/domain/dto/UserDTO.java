package pl.skyterix.sadsky.user.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UserDTO {

    private long id;

    private UUID userId;

    private String firstName;

    private LocalDate birthDay;

    private Integer wakeHour;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PredictionDTO> predictions;

    private String lastName;

    private String email;

    private String encryptedPassword;

    private String password;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GroupStrategy group;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserDTO> friends;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserDTO> friendPendingInvites;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserDTO> friendsTo;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserDTO> friendSentInvites;

    private LocalDateTime lastTokenRevokeDate;

    private LocalDateTime createDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;
}
