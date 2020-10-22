package pl.skyterix.sadsky.user.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Skyte
 */
@Data
@NoArgsConstructor
public class UserReplaceRequest implements Serializable {

    @NotBlank
    @Size(min = 2, max = 255)
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 255)
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String lastName;

    @NotNull
    private LocalDate birthDay;

    @NotNull
    @Range(min = 0, max = 23)
    private Short wakeHour;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
