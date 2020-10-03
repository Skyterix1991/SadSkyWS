package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.user.domain.dto.UserDTO;

import java.util.UUID;

interface CreateUserPort {

    UUID createUser(UserDTO userDTO);

}
