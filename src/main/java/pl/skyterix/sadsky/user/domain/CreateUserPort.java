package pl.skyterix.sadsky.user.domain;

import pl.skyterix.sadsky.user.domain.dto.UserDTO;

import java.util.UUID;

interface CreateUserPort {

    UUID createUser(UserDTO userDTO);

}
