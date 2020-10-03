package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.user.domain.dto.UserDTO;

import java.util.UUID;

interface UpdateUserPort {
    void updateUser(UUID userId, UserDTO userDTO);
}
