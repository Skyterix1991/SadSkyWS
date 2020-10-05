package pl.skyterix.sadsky.user.domain;

import java.util.UUID;

interface UserRepositoryPort {

    boolean existsByEmail(String email);

    User createUser(User user);

    void updateUser(User user);

    void replaceUser(User user);

    void deleteByUserId(UUID userId);

    User findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
