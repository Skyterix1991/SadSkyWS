package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.skyterix.sadsky.exception.AgeNotMeetingRequiredException;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordAlreadyExistsException;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

import static pl.skyterix.sadsky.user.domain.User.MAX_AGE;
import static pl.skyterix.sadsky.user.domain.User.MIN_AGE;

@RequiredArgsConstructor
class ReplaceUserAdapter implements ReplaceUserPort {

    private final UserRepositoryPort userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void replaceUser(UUID userId, UserDTO userDTO) {
        LocalDateTime currentTime = LocalDateTime.now();

        User user = userRepository.findByUserId(userId);

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RecordAlreadyExistsException(Errors.RECORD_ALREADY_EXISTS.getErrorMessage(userDTO.getEmail()));
        }

        int age = calculateAge(userDTO.getBirthDay());

        // Checks is age between 16 and 100
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new AgeNotMeetingRequiredException(Errors.AGE_NOT_MEETING_REQUIRED.getErrorMessage());
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBirthDay(userDTO.getBirthDay());
        user.setEmail(userDTO.getEmail());
        user.setWakeHour(userDTO.getWakeHour());
        user.setLastTokenRevokeDate(currentTime);

        String encryptedPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());

        user.setEncryptedPassword(encryptedPassword);

        userRepository.replaceUser(user);
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
