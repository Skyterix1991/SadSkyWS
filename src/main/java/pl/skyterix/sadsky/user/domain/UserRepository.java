package pl.skyterix.sadsky.user.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
@Repository
@Transactional(rollbackOn = Exception.class, dontRollbackOn = RuntimeException.class)
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUserId(UUID userId);

    boolean existsByEmail(String email);

    boolean existsByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    @Query("select user.friends from User user where user.userId = ?1")
    List<User> findAllUserFriends(UUID userId);

    @Query("select user.friendsTo from User user where user.userId = ?1")
    List<User> findAllUserFriendsTo(UUID userId);

    @Query("select user.friendPendingInvites from User user where user.userId = ?1")
    List<User> findAllUserPendingInvites(UUID userId);

    @Query("select user.friendSentInvites from User user where user.userId = ?1")
    List<User> findAllUserSentInvites(UUID userId);
}
