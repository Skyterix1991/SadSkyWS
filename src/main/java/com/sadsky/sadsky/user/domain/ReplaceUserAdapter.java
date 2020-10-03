package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.exception.Errors;
import com.sadsky.sadsky.exception.RecordAlreadyExistsException;
import com.sadsky.sadsky.user.domain.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
class ReplaceUserAdapter implements ReplaceUserPort {

    private final UserRepositoryPort userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void replaceUser(UUID userId, UserDTO userDTO) {
        LocalDateTime currentTime = LocalDateTime.now();

        User user = userRepository.findByUserId(userId);

        if (userRepository.existsByEmail(userDTO.getEmail()))
            throw new RecordAlreadyExistsException(Errors.RECORD_ALREADY_EXISTS.getErrorMessage(userDTO.getEmail()));

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setLastTokenRevokeDate(currentTime);

        String encryptedPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());

        user.setEncryptedPassword(encryptedPassword);

        userRepository.replaceUser(user);
    }
}
