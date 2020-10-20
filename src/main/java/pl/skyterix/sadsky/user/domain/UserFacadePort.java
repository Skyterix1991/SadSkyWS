package pl.skyterix.sadsky.user.domain;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;

import java.util.List;
import java.util.UUID;

interface UserFacadePort {
    Page<UserDTO> getFullUsers(Predicate predicate, Pageable pageRequest);

    Page<UserDTO> getMiniUsers(Predicate predicate, Pageable pageRequest);

    UserDTO getFullUser(UUID userId);

    UserDTO getMiniUser(UUID userId);

    void updateUser(UUID userId, UserDTO userDTO);

    void replaceUser(UUID userId, UserDTO userDTO);

    UUID createUser(UserDTO userDTO);

    void deleteUser(UUID userId);

    User getAuthenticatedUser();

    void setUserGroup(UUID userId, GroupStrategy groupStrategy);

    List<UserDTO> getUserMiniFriends(UUID userId);

    List<UserDTO> getUserMiniFriendsTo(UUID userId);

    List<UserDTO> getUserMiniPendingInvites(UUID userId);

    List<UserDTO> getUserMiniSentInvites(UUID userId);

    List<UserDTO> getUserFullFriends(UUID userId);

    List<UserDTO> getUserFullFriendsTo(UUID userId);

    List<UserDTO> getUserFullPendingInvites(UUID userId);

    List<UserDTO> getUserFullSentInvites(UUID userId);

    void addUserToFriendsTo(UUID userId, UUID friendId);

    void removeUserFromFriendsTo(UUID userId, UUID friendId);

    void acceptUserPendingInvite(UUID userId, UUID friendId);

    void refuseUserPendingInvite(UUID userId, UUID friendId);

    void removeUserFromFriends(UUID userId, UUID friendId);

    void cancelSentInvite(UUID userId, UUID friendId);
}
