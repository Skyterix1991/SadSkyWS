package pl.skyterix.sadsky.user.domain;

import pl.skyterix.sadsky.user.domain.dto.UserDTO;

import java.util.UUID;

interface UpdateUserPort {
    void updateUser(UUID userId, UserDTO userDTO);
}
