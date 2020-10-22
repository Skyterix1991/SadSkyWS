package pl.skyterix.sadsky.user.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Skyte
 */
@Data
@NoArgsConstructor
public class UserUpdateRequest implements Serializable {

    @Size(min = 2, max = 255)
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String firstName;

    @Size(min = 2, max = 255)
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String lastName;

    private LocalDate birthDay;

    @Range(min = 0, max = 23)
    private Short wakeHour;

    @Email
    @Size(max = 255)
    private String email;

    @Size(min = 6, max = 40)
    private String password;
}
