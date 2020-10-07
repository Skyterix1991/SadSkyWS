package pl.skyterix.sadsky.user.domain;

import java.util.UUID;

interface DeleteUserPort {
    void deleteUser(UUID userId);
}
