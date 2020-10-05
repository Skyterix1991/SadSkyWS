package pl.skyterix.sadsky.user.domain;

import pl.skyterix.sadsky.user.domain.dto.UserDTO;

import java.util.UUID;

interface ReplaceUserPort {
    void replaceUser(UUID userId, UserDTO userDTO);
}
