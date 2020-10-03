package com.sadsky.sadsky.user.domain;

import com.sadsky.sadsky.user.response.UserMiniDetailsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(excerptProjection = UserMiniDetailsResponse.class)
interface MiniUserRepository extends JpaRepository<User, Long> {
    Optional<UserMiniDetailsResponse> findUserByUserId(UUID userId);

    Page<UserMiniDetailsResponse> findAllProjectedBy(Pageable pageable);
}
