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
import pl.skyterix.sadsky.exception.RecordAlreadyExistsException;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.user.domain.group.strategy.AdminGroup;
import pl.skyterix.sadsky.util.JpaModelMapper;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

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
}