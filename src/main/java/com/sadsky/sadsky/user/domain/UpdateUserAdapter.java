package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.exception.AgeNotMeetingRequired;
import com.sadsky.sadsky.exception.Errors;
import com.sadsky.sadsky.exception.RecordAlreadyExistsException;
import com.sadsky.sadsky.user.domain.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

@RequiredArgsConstructor
class UpdateUserAdapter implements UpdateUserPort {

    private final UserRepositoryPort userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void updateUser(UUID userId, UserDTO userDTO) {
        LocalDateTime currentTime = LocalDateTime.now();

        User user = userRepository.findByUserId(userId);

        if (userRepository.existsByEmail(userDTO.getEmail()))
            throw new RecordAlreadyExistsException(Errors.RECORD_ALREADY_EXISTS.getErrorMessage(userDTO.getEmail()));

        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }

        if (userDTO.getLastName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }

        if (userDTO.getBirthDay() != null) {
            int age = calculateAge(userDTO.getBirthDay());

            // Checks is age between 16 and 100
            if (age < 16 || age > 100)
                throw new AgeNotMeetingRequired(Errors.AGE_NOT_MEETING_REQUIRED.getErrorMessage());

            user.setBirthDay(userDTO.getBirthDay());
        }

        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
            user.setLastTokenRevokeDate(currentTime);
        }

        if (userDTO.getPassword() != null) {
            String encryptedPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());

            user.setEncryptedPassword(encryptedPassword);
            user.setLastTokenRevokeDate(currentTime);
        }

        userRepository.updateUser(user);
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
