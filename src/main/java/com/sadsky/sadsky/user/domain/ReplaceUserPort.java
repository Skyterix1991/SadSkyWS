package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.user.domain.dto.UserDTO;

import java.util.UUID;

interface ReplaceUserPort {
    void replaceUser(UUID userId, UserDTO userDTO);
}
