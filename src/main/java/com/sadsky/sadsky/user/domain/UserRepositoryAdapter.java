package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.exception.Errors;
import com.sadsky.sadsky.exception.RecordAlreadyExistsException;
import com.sadsky.sadsky.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new RecordAlreadyExistsException(Errors.RECORD_ALREADY_EXISTS.getErrorMessage(user.getEmail()));

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
        if (!userRepository.existsByUserId(userId))
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(userId.toString()));

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
