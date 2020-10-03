package com.sadsky.sadsky.user.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUserId(UUID userId);

    boolean existsByEmail(String email);

    boolean existsByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
