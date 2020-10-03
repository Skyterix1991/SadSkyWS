package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.user.domain.dto.UserDTO;
import com.sadsky.sadsky.util.JpaModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@RequiredArgsConstructor
class CreateUserAdapter implements CreateUserPort {

    private final UserRepositoryPort userRepositoryAdapter;
    private final JpaModelMapper jpaModelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UUID createUser(UserDTO userDTO) {
        String encryptedPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());

        userDTO.setEncryptedPassword(encryptedPassword);

        User user = userRepositoryAdapter.createUser(jpaModelMapper.mapEntity(userDTO, User.class));

        return user.getUserId();
    }
}
