package pl.skyterix.sadsky.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Skyte
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginRequest implements Serializable {

    @Email
    @NotBlank
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(max = 40)
    private String password;
}