package pl.skyterix.sadsky.user.domain;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.skyterix.sadsky.exception.FriendsCountExceededMaximumException;
import pl.skyterix.sadsky.exception.PendingFriendInvitesExceededMaximumException;
import pl.skyterix.sadsky.exception.RecordAlreadyExistsException;
import pl.skyterix.sadsky.exception.RecordAlreadyExistsInCollectionException;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.exception.RecordNotFoundInCollectionException;
import pl.skyterix.sadsky.exception.SentFriendInvitesExceededMaximumException;
import pl.skyterix.sadsky.exception.TargetRecordIsTheSameAsSourceException;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.user.domain.group.strategy.AdminGroup;
import pl.skyterix.sadsky.util.JpaModelMapper;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserFacadeTest {

    @Autowired
    private JpaModelMapper jpaModelMapper;

    @Autowired
    private UserFacade userFacade;

    @MockBean
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void beforeEach() {
        userDTO = new UserDTO();
        userDTO.setUserId(UUID.randomUUID());
        userDTO.setFirstName("Jan");
        userDTO.setLastName("Kowalski");
        userDTO.setBirthDay(LocalDate.of(2000, 5, 20));
        userDTO.setEmail("Test@Test.pl");
        userDTO.setPassword("Test");
        userDTO.setWakeHour(User.DEFAULT_WAKE_HOUR);
        userDTO.setGroup(new AdminGroup());

        user = jpaModelMapper.mapEntity(userDTO, User.class);
    }

    @Test
    @DisplayName("Create user")
    void givenUserDTO_whenCreateUser_thenReturnUserId() {
        // Given
        // When
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        // Then
        assertEquals(user.getUserId(), userFacade.createUser(userDTO), "UserId is not the same as original.");
    }

    @Test
    @DisplayName("Create user with existing email")
    void givenUserDTOWithExistingEmail_whenCreateUser_thenThrowRecordAlreadyExistsException() {
        // Given
        // When
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);
        // Then
        assertThrows(RecordAlreadyExistsException.class, () -> userFacade.createUser(userDTO), "RecordAlreadyExistsException was not thrown.");
    }

    @Test
    @DisplayName("Delete user with non existing uuid")
    void givenNonExistingUUID_whenDeleteUser_thenThrowRecordNotFoundException() {
        // Given
        // When
        when(userRepository.existsByUserId(any())).thenReturn(false);
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.deleteUser(UUID.randomUUID()), "RecordNotFoundException was not thrown.");
    }

    @Test
    @DisplayName("Delete user")
    void whenDeleteUser_thenRunSuccessfully() {
        // Given
        // When
        when(userRepository.existsByUserId(any())).thenReturn(true);
        // Then
        assertDoesNotThrow(() -> userFacade.deleteUser(UUID.randomUUID()), "Exception was thrown.");
    }


    @Test
    @DisplayName("Update user")
    void givenUserId_whenUpdateUser_thenRunSuccessfully() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.of(new User()));
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        // Then
        assertDoesNotThrow(() -> userFacade.updateUser(UUID.randomUUID(), userDTO), "Exception was thrown.");
    }

    @Test
    @DisplayName("Update user with non existing uuid")
    void givenNonExistingUserId_whenUpdateUser_thenThrowRecordNotFoundException() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.updateUser(UUID.randomUUID(), userDTO), "RecordNotFoundException was not thrown.");
    }

    @Test
    @DisplayName("Update user with existing email")
    void givenExistingEmail_whenUpdateUser_thenThrowRecordAlreadyExistsException() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.of(new User()));
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);
        // Then
        assertThrows(RecordAlreadyExistsException.class, () -> userFacade.updateUser(UUID.randomUUID(), userDTO), "RecordAlreadyExistsException was not thrown.");
    }

    @Test
    @DisplayName("Replace user")
    void givenUserId_whenReplaceUser_thenRunSuccessfully() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.of(new User()));
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        // Then
        assertDoesNotThrow(() -> userFacade.replaceUser(UUID.randomUUID(), userDTO), "Exception was thrown.");
    }

    @Test
    @DisplayName("Replace user with non existing uuid")
    void givenNonExistingUserId_whenReplaceUser_thenThrowRecordNotFoundException() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.replaceUser(UUID.randomUUID(), userDTO), "RecordNotFoundException was not thrown.");
    }

    @Test
    @DisplayName("Replace user with existing email")
    void givenExistingEmail_whenReplaceUser_thenThrowRecordAlreadyExistsException() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.of(new User()));
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);
        // Then
        assertThrows(RecordAlreadyExistsException.class, () -> userFacade.replaceUser(UUID.randomUUID(), userDTO), "RecordAlreadyExistsException was not thrown.");
    }

    @Test
    @DisplayName("Get full users")
    void givenPredicateAndPageable_whenGetFullUsers_thenReturnValidUsersList() {
        // Given
        // Needs to be manually created mockito not working with just any(Predicate.class)
        Predicate predicate = new Predicate() {
            @Override
            public Predicate not() {
                return null;
            }

            @Nullable
            @Override
            public <R, C> R accept(Visitor<R, C> visitor, @Nullable C c) {
                return null;
            }

            @Override
            public Class<? extends Boolean> getType() {
                return null;
            }
        };

        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("desc"));
        // When
        when(userRepository.findAll(predicate, pageRequest)).thenReturn(new PageImpl<>(Collections.singletonList(user)));
        // Then
        assertEquals(
                new PageImpl<>(Collections.singletonList(userDTO)).getSize(),
                userFacade.getFullUsers(predicate, pageRequest).getSize()
                , "Received list length is not the same as original.");
    }

    @Test
    @DisplayName("Get mini users")
    void givenPredicateAndPageable_whenGetMiniUsers_thenReturnValidUsersList() {
        // Given
        // Needs to be manually created mockito not working with just any(Predicate.class)
        Predicate predicate = new Predicate() {
            @Override
            public Predicate not() {
                return null;
            }

            @Nullable
            @Override
            public <R, C> R accept(Visitor<R, C> visitor, @Nullable C c) {
                return null;
            }

            @Override
            public Class<? extends Boolean> getType() {
                return null;
            }
        };

        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("desc"));
        // When
        when(userRepository.findAll(predicate, pageRequest)).thenReturn(new PageImpl<>(Collections.singletonList(user)));
        // Then
        Page<UserDTO> users = userFacade.getMiniUsers(predicate, pageRequest);

        assertAll(() -> {
            assertEquals(
                    new PageImpl<>(Collections.singletonList(userDTO)).getSize(),
                    users.getSize(),
                    "Received list length is not the same as original.");
            assertNull(users.getContent().get(0).getLastName(), "Sensitive data is exposed.");
        });
    }

    @Test
    @DisplayName("Get full user")
    void givenUserId_whenGetFullUser_thenReturnValidUser() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.of(user));
        // Then
        userDTO.setPassword(null);

        assertEquals(userDTO, userFacade.getFullUser(UUID.randomUUID()), "Received user is not the same as original.");
    }

    @Test
    @DisplayName("Get full user by non existing uuid")
    void givenNonExistingUserId_whenGetFullUser_thenReturnValidUser() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.getFullUser(UUID.randomUUID()), "RecordNotFoundException was not thrown.");
    }

    @Test
    @DisplayName("Get mini user")
    void givenUserId_whenGetMiniUser_thenReturnValidUser() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.of(user));
        // Then
        userDTO.setPassword(null);

        UserDTO miniUser = userFacade.getMiniUser(UUID.randomUUID());

        assertAll(() -> {
            assertNull(miniUser.getLastName(), "Sensitive data is exposed.");
        });
    }

    @Test
    @DisplayName("Get mini user by non existing uuid")
    void givenNonExistingUserId_whenGetMiniUser_thenReturnValidUser() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.getMiniUser(UUID.randomUUID()), "RecordNotFoundException was not thrown.");
    }

    @Test
    @DisplayName("Get authenticated user")
    void whenGetAuthenticatedUser_thenReturnCurrentUser() {
        // Given
        // When
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(UUID.randomUUID().toString());
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.of(user));
        // Then
        assertEquals(user, userFacade.getAuthenticatedUser(), "Result is not the same as original.");
    }

    @Test
    @DisplayName("Set user group")
    void givenUserIdAndGroupName_whenSetUserGroup_thenRunSuccessfully() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        // Then
        assertDoesNotThrow(() -> userFacade.setUserGroup(UUID.randomUUID(), new AdminGroup()), "Exception was thrown.");
    }

    @Test
    @DisplayName("Set user group with non existing uuid")
    void givenNonExistingUserIdAndGroupName_whenSetUserGroup_thenThrowRecordNotFoundException() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.setUserGroup(UUID.randomUUID(), new AdminGroup()), "Non existing uuid was given but exception was not thrown.");
    }

    @Test
    @DisplayName("Get user mini friends")
    void getUserMiniFriends() {
        // Given
        userDTO.setPassword(null);
        // When
        when(userRepository.findAllUserFriends(any())).thenReturn(Collections.singletonList(user));
        // Then
        List<UserDTO> users = userFacade.getUserMiniFriends(UUID.randomUUID());

        assertNull(users.get(0).getEmail(), "Sensitive data is exposed.");
    }

    @Test
    @DisplayName("Get user mini friends to")
    void givenUserId_getUserMiniFriendsTo() {
        // Given
        userDTO.setPassword(null);
        // When
        when(userRepository.findAllUserFriendsTo(any())).thenReturn(Collections.singletonList(user));
        // Then
        List<UserDTO> users = userFacade.getUserMiniFriendsTo(UUID.randomUUID());

        assertNull(users.get(0).getEmail(), "Sensitive data is exposed.");
    }

    @Test
    @DisplayName("Get user mini pending invites")
    void getUserMiniPendingInvites() {
        // Given
        userDTO.setPassword(null);
        // When
        when(userRepository.findAllUserPendingInvites(any())).thenReturn(Collections.singletonList(user));
        // Then
        List<UserDTO> users = userFacade.getUserMiniPendingInvites(UUID.randomUUID());

        assertNull(users.get(0).getEmail(), "Sensitive data is exposed.");
    }

    @Test
    @DisplayName("Get user mini sent invites")
    void givenUserId_whenGetUserMiniSentInvites_thenReturnUserMiniSentInvitesWithoutSensitiveData() {
        // Given
        userDTO.setPassword(null);
        // When
        when(userRepository.findAllUserSentInvites(any())).thenReturn(Collections.singletonList(user));
        // Then
        List<UserDTO> users = userFacade.getUserMiniSentInvites(UUID.randomUUID());

        assertNull(users.get(0).getEmail(), "Sensitive data is exposed.");
    }

    @Test
    @DisplayName("Get user full friends")
    void givenUserId_whenGetUserFullFriends_thenReturnValidUserFriends() {
        // Given
        userDTO.setPassword(null);
        // When
        when(userRepository.findAllUserFriends(any())).thenReturn(Collections.singletonList(user));
        // Then
        assertEquals(Collections.singletonList(userDTO), userFacade.getUserFullFriends(UUID.randomUUID()), "Result is not the same as original.");
    }

    @Test
    @DisplayName("Get user full friends to")
    void givenUserId_whenGetUserFullFriendsTo_thenReturnValidUserFriendsTo() {
        // Given
        userDTO.setPassword(null);
        // When
        when(userRepository.findAllUserFriendsTo(any())).thenReturn(Collections.singletonList(user));
        // Then
        assertEquals(Collections.singletonList(userDTO), userFacade.getUserFullFriendsTo(UUID.randomUUID()), "Result is not the same as original.");
    }

    @Test
    @DisplayName("Get user full pending invites")
    void givenUserId_whenGetUserFullPendingInvites_thenReturnValidUserPendingInvites() {
        // Given
        userDTO.setPassword(null);
        // When
        when(userRepository.findAllUserPendingInvites(any())).thenReturn(Collections.singletonList(user));
        // Then
        assertEquals(Collections.singletonList(userDTO), userFacade.getUserFullPendingInvites(UUID.randomUUID()), "Result is not the same as original.");
    }

    @Test
    @DisplayName("Get user full sent invites")
    void givenUserId_whenGetUserFullSentInvites_thenReturnValidUserSentInvites() {
        // Given
        userDTO.setPassword(null);
        // When
        when(userRepository.findAllUserSentInvites(any())).thenReturn(Collections.singletonList(user));
        // Then
        assertEquals(Collections.singletonList(userDTO), userFacade.getUserFullSentInvites(UUID.randomUUID()), "Result is not the same as original.");
    }

    @Test
    @DisplayName("Add user to friends to")
    void givenUserIdAndFriendId_whenAddUserToFriendsTo_thenRunSuccessfully() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());

        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertDoesNotThrow(() -> userFacade.addUserToFriendsTo(user.getUserId(), friend.getUserId()), "Exception was thrown.");
    }

    @Test
    @DisplayName("Add user to friends to with non existing user uuid")
    void givenNonExistingUserIdAndFriendId_whenAddUserToFriendsTo_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.empty());
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.addUserToFriendsTo(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Add user to friends to with same uuid as friend")
    void givenUserIdSameAsFriendIdAndFriendId_whenAddUserToFriendsTo_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        // Then
        assertThrows(TargetRecordIsTheSameAsSourceException.class, () -> userFacade.addUserToFriendsTo(friend.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Add user to friends to with non existing friend uuid")
    void givenUserIdAndNonExistingFriendId_whenAddUserToFriendsTo_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.addUserToFriendsTo(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Add user to friends to with already sent invite")
    void givenUserIdWithAlreadySentInviteAndFriendId_whenAddUserToFriendsTo_thenThrowRecordAlreadyExistsInCollectionException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());

        user.setFriendSentInvites(Collections.singletonList(friend));
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertThrows(RecordAlreadyExistsInCollectionException.class, () -> userFacade.addUserToFriendsTo(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Add user to friends to with full sent invites list")
    void givenUserIdWithFullSentInvitesListAndFriendId_whenAddUserToFriendsTo_thenThrowPendingFriendInvitesExceededMaximumException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());

        user.setFriendSentInvites(Arrays.asList(user, user, user, user, user, user, user, user, user, user, user));
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertThrows(SentFriendInvitesExceededMaximumException.class, () -> userFacade.addUserToFriendsTo(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Add user to friends to with full pending invites list")
    void givenUserIdAndFriendIdWithFullPendingInvitesList_whenAddUserToFriendsTo_thenThrowSentFriendInvitesExceededMaximumException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());

        friend.setFriendPendingInvites(Arrays.asList(user, user, user, user, user, user, user, user, user, user, user));
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertThrows(PendingFriendInvitesExceededMaximumException.class, () -> userFacade.addUserToFriendsTo(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Remove user from friends")
    void givenUserIdWithFriendInFriendsToAndFriendId_whenRemoveUserFromFriends_thenRunSuccessfully() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        friend.setFriendsTo(new ArrayList<>());

        user.setFriends(new ArrayList<>(Collections.singletonList(friend)));
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertDoesNotThrow(() -> userFacade.removeUserFromFriends(user.getUserId(), friend.getUserId()), "Exception was thrown.");
    }

    @Test
    @DisplayName("Remove user from friends using same uuid as friend")
    void givenUserIdSameAsFriendIdAndFriendId_whenRemoveUserFromFriends_thenThrownTargetRecordIsTheSameAsSourceException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        // Then
        assertThrows(TargetRecordIsTheSameAsSourceException.class, () -> userFacade.removeUserFromFriends(friend.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Remove user from friends using same uuid as friend")
    void givenUserIdWithoutFriendInFriendsAndFriendId_whenRemoveUserFromFriends_thenThrownRecordNotFoundInCollectionException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertThrows(RecordNotFoundInCollectionException.class, () -> userFacade.removeUserFromFriends(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Remove user from friends with non existing user uuid")
    void givenNonExistingUserIdAndFriendId_whenRemoveUserFromFriends_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.removeUserFromFriends(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Remove user from friends with non existing friend uuid")
    void givenUserIdAndNonExistingFriendId_whenRemoveUserFromFriends_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.removeUserFromFriends(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Remove user from friends to")
    void givenUserIdWithFriendInFriendsToAndFriendId_whenRemoveUserFromFriendsTo_thenRunSuccessfully() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());

        user.setFriendsTo(new ArrayList<>(Collections.singletonList(friend)));
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertDoesNotThrow(() -> userFacade.removeUserFromFriendsTo(user.getUserId(), friend.getUserId()), "Exception was thrown.");
    }

    @Test
    @DisplayName("Remove user from friends to using same uuid as friend")
    void givenUserIdSameAsFriendIdAndFriendId_whenRemoveUserFromFriendsTo_thenThrownTargetRecordIsTheSameAsSourceException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        // Then
        assertThrows(TargetRecordIsTheSameAsSourceException.class, () -> userFacade.removeUserFromFriendsTo(friend.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Remove user from friends to using same uuid as friend")
    void givenUserIdWithoutFriendInFriendsToAndFriendId_whenRemoveUserFromFriendsTo_thenThrownRecordNotFoundInCollectionException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertThrows(RecordNotFoundInCollectionException.class, () -> userFacade.removeUserFromFriendsTo(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Remove user from friends to with non existing user uuid")
    void givenNonExistingUserIdAndFriendId_whenRemoveUserFromFriendsTo_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.removeUserFromFriendsTo(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Remove user from friends to with non existing friend uuid")
    void givenUserIdAndNonExistingFriendId_whenRemoveUserFromFriendsTo_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.removeUserFromFriendsTo(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Cancel user sent invite")
    void givenUserIdWithFriendInFriendSentInvitesAndFriendId_whenCancelUserSentInvite_thenRunSuccessfully() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());

        user.setFriendSentInvites(new ArrayList<>(Collections.singletonList(friend)));
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertDoesNotThrow(() -> userFacade.cancelSentInvite(user.getUserId(), friend.getUserId()), "Exception was thrown.");
    }

    @Test
    @DisplayName("Cancel user sent invite using same uuid as friend")
    void givenUserIdSameAsFriendIdAndFriendId_whenCancelUserSentInvite_thenThrownTargetRecordIsTheSameAsSourceException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        // Then
        assertThrows(TargetRecordIsTheSameAsSourceException.class, () -> userFacade.cancelSentInvite(friend.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Cancel user sent invite using same uuid as friend")
    void givenUserIdWithoutFriendInFriendsToAndFriendId_whenCancelUserSentInvite_thenThrownRecordNotFoundInCollectionException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertThrows(RecordNotFoundInCollectionException.class, () -> userFacade.cancelSentInvite(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Cancel user sent invite with non existing user uuid")
    void givenNonExistingUserIdAndFriendId_whenCancelUserSentInvite_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.cancelSentInvite(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Cancel user sent invite with non existing friend uuid")
    void givenUserIdAndNonExistingFriendId_whenCancelUserSentInvite_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.cancelSentInvite(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Test
    @DisplayName("Refuse user pending invite")
    void givenUserIdWithFriendInFriendPendingInvitesAndFriendId_whenRefuseUserPendingInvite_thenRunSuccessfully() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());

        user.setFriendPendingInvites(new ArrayList<>(Collections.singletonList(friend)));
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertDoesNotThrow(() -> userFacade.refuseUserPendingInvite(user.getUserId(), friend.getUserId()), "Exception was thrown.");
    }

    @Test
    @DisplayName("Cancel user sent invite using same uuid as friend")
    void givenUserIdSameAsFriendIdAndFriendId_whenRefuseUserPendingInvite_thenThrownTargetRecordIsTheSameAsSourceException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        // Then
        assertThrows(TargetRecordIsTheSameAsSourceException.class, () -> userFacade.refuseUserPendingInvite(friend.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Cancel user sent invite using same uuid as friend")
    void givenUserIdWithoutFriendInFriendsToAndFriendId_whenRefuseUserPendingInvite_thenThrownRecordNotFoundInCollectionException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertThrows(RecordNotFoundInCollectionException.class, () -> userFacade.refuseUserPendingInvite(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Cancel user sent invite with non existing user uuid")
    void givenNonExistingUserIdAndFriendId_whenRefuseUserPendingInvite_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.refuseUserPendingInvite(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Cancel user sent invite with non existing friend uuid")
    void givenUserIdAndNonExistingFriendId_whenRefuseUserPendingInvite_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.refuseUserPendingInvite(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Test
    @DisplayName("Accept user pending invite")
    void givenUserIdAndFriendId_whenAcceptUserPendingInvite_thenRunSuccessfully() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());

        user.setFriendPendingInvites(new ArrayList<>(Collections.singletonList(friend)));
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertDoesNotThrow(() -> userFacade.acceptUserPendingInvite(user.getUserId(), friend.getUserId()), "Exception was thrown.");
    }

    @Test
    @DisplayName("Accept user pending invite with non existing user uuid")
    void givenNonExistingUserIdAndFriendId_whenAcceptUserPendingInvite_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.empty());
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.acceptUserPendingInvite(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Accept user pending invite with same uuid as friend")
    void givenUserIdSameAsFriendIdAndFriendId_whenAcceptUserPendingInvite_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        // Then
        assertThrows(TargetRecordIsTheSameAsSourceException.class, () -> userFacade.acceptUserPendingInvite(friend.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Accept user pending invite with non existing friend uuid")
    void givenUserIdAndNonExistingFriendId_whenAcceptUserPendingInvite_thenThrownRecordNotFoundException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.acceptUserPendingInvite(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Accept user pending invite with full friends list")
    void givenUserIdWithFullFriendsListAndFriendId_whenAcceptUserPendingInvite_thenThrowFriendsCountExceededMaximumException() {
        // Given
        User friend = new User();
        friend.setUserId(UUID.randomUUID());

        user.setFriendPendingInvites(new ArrayList<>(Collections.singletonList(friend)));

        List<User> friends = IntStream.range(0, 51)
                .mapToObj(__ -> user)
                .collect(Collectors.toList());

        user.setFriends(friends);
        // When
        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUserId(friend.getUserId())).thenReturn(Optional.of(friend));
        // Then
        assertThrows(FriendsCountExceededMaximumException.class, () -> userFacade.acceptUserPendingInvite(user.getUserId(), friend.getUserId()), "Exception was not thrown.");
    }
}