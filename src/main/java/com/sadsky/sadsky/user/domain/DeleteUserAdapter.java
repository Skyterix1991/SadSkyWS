package com.sadsky.sadsky.user.domain;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
class DeleteUserAdapter implements DeleteUserPort {

    private final UserRepositoryPort userRepositoryAdapter;

    @Override
    public void deleteUser(UUID userId) {
        userRepositoryAdapter.deleteByUserId(userId);
    }
}
