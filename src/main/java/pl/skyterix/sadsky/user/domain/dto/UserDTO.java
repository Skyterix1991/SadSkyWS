package pl.skyterix.sadsky.user.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDTO {

    private long id;

    private UUID userId;

    private String firstName;

    private LocalDate birthDay;

    private Short wakeHour;

    private String lastName;

    private String email;

    private String encryptedPassword;

    private String password;

    private GroupStrategy group;

    private LocalDateTime lastTokenRevokeDate;

    private LocalDateTime createDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;
}