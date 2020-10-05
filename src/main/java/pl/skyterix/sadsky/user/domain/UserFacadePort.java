package pl.skyterix.sadsky.user.domain;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;

import java.util.UUID;

interface UserFacadePort {
    Page<UserDTO> getUsers(Predicate predicate, Pageable pageRequest);

    UserDTO getUser(UUID userId);

    void updateUser(UUID userId, UserDTO userDTO);

    void replaceUser(UUID userId, UserDTO userDTO);

    UUID createUser(UserDTO userDTO);

    void deleteUser(UUID userId);

    User getAuthenticatedUser();

    void setUserGroup(UUID userId, GroupStrategy groupStrategy);
}
