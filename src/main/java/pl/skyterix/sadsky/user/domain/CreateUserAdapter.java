package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.skyterix.sadsky.exception.AgeNotMeetingRequiredException;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.UUID;

import static pl.skyterix.sadsky.user.domain.User.MAX_AGE;
import static pl.skyterix.sadsky.user.domain.User.MIN_AGE;

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
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new AgeNotMeetingRequiredException(Errors.AGE_NOT_MEETING_REQUIRED.getErrorMessage());
        }

        // Create default prediction
        PredictionDTO predictionDTO = new PredictionDTO();
        predictionDTO.setOwner(userDTO);

        userDTO.setPredictions(Collections.singletonList(predictionDTO));

        User user = userRepositoryAdapter.createUser(jpaModelMapper.mapEntity(userDTO, User.class));

        return user.getUserId();
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
