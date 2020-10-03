package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.exception.AgeNotMeetingRequired;
import com.sadsky.sadsky.exception.Errors;
import com.sadsky.sadsky.user.domain.dto.UserDTO;
import com.sadsky.sadsky.util.JpaModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.Period;
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

        int age = calculateAge(userDTO.getBirthDay());

        // Checks is age between 16 and 100
        if (age < 16 || age > 100)
            throw new AgeNotMeetingRequired(Errors.AGE_NOT_MEETING_REQUIRED.getErrorMessage());

        User user = userRepositoryAdapter.createUser(jpaModelMapper.mapEntity(userDTO, User.class));

        return user.getUserId();
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
