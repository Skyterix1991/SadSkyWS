package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordAlreadyExistsException;
import pl.skyterix.sadsky.exception.RecordNotFoundException;

import javax.transaction.Transactional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class, dontRollbackOn = RuntimeException.class)
class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RecordAlreadyExistsException(Errors.RECORD_ALREADY_EXISTS.getErrorMessage(user.getEmail()));
        }

        return userRepository.save(user);
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void replaceUser(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(userId.toString()));
        }

        userRepository.deleteByUserId(userId);
    }

    @Override
    public User findByUserId(UUID userId) {
        return userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(userId.toString())));
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return userRepository.existsByUserId(userId);
    }
}
