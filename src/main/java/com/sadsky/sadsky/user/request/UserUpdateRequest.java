package com.sadsky.sadsky.user.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserUpdateRequest implements Serializable {

    @Email
    @Size(max = 255)
    private String email;

    @Size(min = 6, max = 40)
    private String password;
}
