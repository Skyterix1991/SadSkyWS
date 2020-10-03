package com.sadsky.sadsky.user.domain;

import com.querydsl.core.types.Predicate;
import com.sadsky.sadsky.exception.Errors;
import com.sadsky.sadsky.exception.GroupUnauthorizedException;
import com.sadsky.sadsky.exception.RecordNotFoundException;
import com.sadsky.sadsky.user.domain.dto.MiniUserDTO;
import com.sadsky.sadsky.user.domain.dto.UserDTO;
import com.sadsky.sadsky.user.domain.group.Permission;
import com.sadsky.sadsky.user.domain.group.SelfPermission;
import com.sadsky.sadsky.user.domain.group.strategy.AdminGroup;
import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;
import com.sadsky.sadsky.user.response.UserMiniDetailsResponse;
import com.sadsky.sadsky.util.JpaModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserFacade implements UserFacadePort, CommandLineRunner {

    private final Environment environment;
    private final JpaModelMapper jpaModelMapper;
    private final UserRepository userRepository;
    private final MiniUserRepository miniUserRepository;
    private final CreateUserPort createUserAdapter;
    private final DeleteUserPort deleteUserAdapter;
    private final UpdateUserPort updateUserAdapter;
    private final ReplaceUserPort replaceUserAdapter;
    private final SetUserGroupPort setUserGroupAdapter;

    /**
     * Creates user.
     *
     * @param userDTO User to create.
     * @return Created user UUID.
     */
    @Override
    public UUID createUser(UserDTO userDTO) {
        User currentUser = getAuthenticatedUser();

        if (currentUser.hasPermission(Permission.CREATE_USER)) {
            return createUserAdapter.createUser(userDTO);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

    /**
     * Deletes user.
     *
     * @param userId User UUID.
     */
    @Override
    public void deleteUser(UUID userId) {
        User currentUser = getAuthenticatedUser();

        if (currentUser.hasPermission(userId, SelfPermission.DELETE_SELF_USER, Permission.DELETE_USER)) {
            deleteUserAdapter.deleteUser(userId);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

    /**
     * Get users in Page.
     *
     * @param predicate   Predicate to search with.
     * @param pageRequest Page info.
     * @return Paged result list.
     */
    @Override
    public Page<UserDTO> getUsers(Predicate predicate, Pageable pageRequest) {
        User currentUser = getAuthenticatedUser();

        Page<User> users;

        if (currentUser.hasPermission(Permission.GET_FULL_USERS)) {
            users = userRepository.findAll(predicate, pageRequest);

        } else if (currentUser.hasPermission(Permission.GET_MINI_USERS)) {
            Page<UserMiniDetailsResponse> userMiniDetailsResponsePage = miniUserRepository.findAllProjectedBy(pageRequest);

            Page<MiniUserDTO> miniUserDTOPage = userMiniDetailsResponsePage.stream()
                    .map((miniUser) -> jpaModelMapper.mapEntity(miniUser, MiniUserDTO.class))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> new PageImpl<>(list, pageRequest, userMiniDetailsResponsePage.getTotalElements())));

            users = miniUserDTOPage.stream()
                    .map((miniUser) -> jpaModelMapper.mapEntity(miniUser, User.class))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> new PageImpl<>(list, pageRequest, miniUserDTOPage.getTotalElements())));
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

        return users.stream()
                .map((user) -> jpaModelMapper.mapEntity(user, UserDTO.class))
                .collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> new PageImpl<>(list, pageRequest, users.getTotalElements())));
    }

    /**
     * Get user.
     *
     * @param userId User UUID.
     * @return User with given UUID.
     */
    @Override
    public UserDTO getUser(UUID userId) {
        User currentUser = getAuthenticatedUser();
        User user;

        if (currentUser.hasPermission(userId, SelfPermission.GET_FULL_SELF_USER, Permission.GET_FULL_USER)) {

            user = userRepository.findUserByUserId(userId)
                    .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(userId.toString())));

        } else if (currentUser.hasPermission(userId, SelfPermission.GET_MINI_SELF_USER, Permission.GET_MINI_USER)) {

            UserMiniDetailsResponse userMini = miniUserRepository.findUserByUserId(userId)
                    .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(userId.toString())));

            MiniUserDTO miniUserDTO = jpaModelMapper.mapEntity(userMini, MiniUserDTO.class);

            user = jpaModelMapper.mapEntity(miniUserDTO, User.class);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));

        return jpaModelMapper.mapEntity(user, UserDTO.class);
    }

    /**
     * Updates user.
     *
     * @param userId  User UUID.
     * @param userDTO Updated user.
     */
    @Override
    public void updateUser(UUID userId, UserDTO userDTO) {
        User currentUser = getAuthenticatedUser();

        if (currentUser.hasPermission(userId, SelfPermission.UPDATE_SELF_USER, Permission.UPDATE_USER)) {
            updateUserAdapter.updateUser(userId, userDTO);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

    /**
     * Replaces user.
     *
     * @param userId  User UUID
     * @param userDTO Replacement user.
     */
    @Override
    public void replaceUser(UUID userId, UserDTO userDTO) {
        User currentUser = getAuthenticatedUser();

        if (currentUser.hasPermission(userId, SelfPermission.REPLACE_SELF_USER, Permission.REPLACE_USER)) {
            replaceUserAdapter.replaceUser(userId, userDTO);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

    /**
     * Returns currently logged user.
     *
     * @return Logged user.
     */
    @Override
    public User getAuthenticatedUser() {
        try {
            String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            return userRepository.findUserByUserId(UUID.fromString(userId))
                    .orElseThrow(() -> new IllegalStateException("Authenticated user uuid not found."));
        } catch (NullPointerException e) {
            throw new IllegalStateException("No user authenticated.");
        }
    }

    /**
     * Assigns user group.
     *
     * @param userId        User UUID.
     * @param groupStrategy Group strategy.
     */
    @Override
    public void setUserGroup(UUID userId, GroupStrategy groupStrategy) {
        User currentUser = getAuthenticatedUser();

        if (currentUser.hasPermission(Permission.ASSIGN_GROUP)) {
            setUserGroupAdapter.setUserGroup(userId, groupStrategy);
        } else
            throw new GroupUnauthorizedException(Errors.UNAUTHORIZED_GROUP.getErrorMessage(currentUser.getGroup().getName()));
    }

    @Override
    public void run(String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter((profile) -> profile.equals("dev"))
                .findAny()
                .ifPresent((profile) -> createTestingUsers());
    }

    /**
     * Creates testing users if dev profile is selected
     */
    private void createTestingUsers() {
        UserDTO adminUser = new UserDTO();
        adminUser.setFirstName("Jan");
        adminUser.setLastName("Kowalski");
        adminUser.setBirthDay(LocalDate.of(2000, 5, 20));
        adminUser.setEmail("admin@admin.pl");
        adminUser.setPassword("admin");
        adminUser.setGroup(new AdminGroup());

        createUserAdapter.createUser(adminUser);
    }
}
