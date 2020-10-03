package com.sadsky.sadsky.user.domain;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import com.sadsky.sadsky.exception.RecordAlreadyExistsException;
import com.sadsky.sadsky.exception.RecordNotFoundException;
import com.sadsky.sadsky.user.domain.dto.UserDTO;
import com.sadsky.sadsky.user.domain.group.strategy.AdminGroup;
import com.sadsky.sadsky.util.JpaModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserFacadeTest {

    @PersistenceContext
    private EntityManager entityManager;

    private JpaModelMapper jpaModelMapper;

    @SpyBean
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
        jpaModelMapper = new JpaModelMapper(entityManager);

        userDTO = new UserDTO();
        userDTO.setUserId(UUID.randomUUID());
        userDTO.setFirstName("Jan");
        userDTO.setLastName("Kowalski");
        userDTO.setBirthDay(LocalDate.of(2000, 5, 20));
        userDTO.setEmail("Test@Test.pl");
        userDTO.setPassword("Test");
        userDTO.setGroup(new AdminGroup());

        user = jpaModelMapper.mapEntity(userDTO, User.class);

        mockAuthentication();
    }

    private void mockAuthentication() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(UUID.randomUUID());
        userDTO.setGroup(new AdminGroup());

        User currentUser = jpaModelMapper.mapEntity(userDTO, User.class);

        doReturn(currentUser).when(userFacade).getAuthenticatedUser();
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
    @DisplayName("Get users")
    void givenPredicateAndPageable_whenGetUsers_thenReturnValidUsersList() {
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
                userFacade.getUsers(predicate, pageRequest).getSize()
                , "Received list length is not the same as original.");
    }

    @Test
    @DisplayName("Get user")
    void givenUserId_whenGetUser_thenReturnValidUser() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.of(user));
        // Then
        userDTO.setPassword(null);

        assertEquals(userDTO, userFacade.getUser(UUID.randomUUID()), "Received user is not the same as original.");
    }

    @Test
    @DisplayName("Get user by non existing uuid")
    void givenNonExistingUserId_whenGetUser_thenReturnValidUser() {
        // Given
        // When
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> userFacade.getUser(UUID.randomUUID()), "RecordNotFoundException was not thrown.");
    }

    @Test
    @DisplayName("Get authenticated user")
    void whenGetAuthenticatedUser_thenReturnValidUser() {
        // Given
        // When
        doCallRealMethod().when(userFacade).getAuthenticatedUser();

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