package pl.skyterix.sadsky.user.domain;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.user.domain.dto.MiniUserDTO;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.user.domain.group.strategy.AdminGroup;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserFacade implements UserFacadePort, CommandLineRunner {

    private final Environment environment;
    private final JpaModelMapper jpaModelMapper;
    private final UserRepository userRepository;
    private final CreateUserPort createUserAdapter;
    private final DeleteUserPort deleteUserAdapter;
    private final UpdateUserPort updateUserAdapter;
    private final ReplaceUserPort replaceUserAdapter;
    private final SetUserGroupPort setUserGroupAdapter;

    /**
     * Creates user based on input given in UserDTO validated before by request validators.
     *
     * @param userDTO User to create.
     * @return Created user UUID.
     */
    @Override
    public UUID createUser(UserDTO userDTO) {
        return createUserAdapter.createUser(userDTO);
    }

    /**
     * Deletes user by userId.
     *
     * @param userId User UUID.
     */
    @Override
    public void deleteUser(UUID userId) {
        deleteUserAdapter.deleteUser(userId);
    }

    /**
     * Get users in Page with full details.
     *
     * @param predicate   Predicate to search with.
     * @param pageRequest Page info.
     * @return Paged result list.
     */
    @Override
    public Page<UserDTO> getFullUsers(Predicate predicate, Pageable pageRequest) {
        Page<User> users = userRepository.findAll(predicate, pageRequest);

        return users.stream()
                .map((user) -> jpaModelMapper.mapEntity(user, UserDTO.class))
                .collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> new PageImpl<>(list, pageRequest, users.getTotalElements())));
    }

    /**
     * Get users in Page with reduced details.
     *
     * @param predicate   Predicate to search with.
     * @param pageRequest Page info.
     * @return Paged result list.
     */
    @Override
    public Page<UserDTO> getMiniUsers(Predicate predicate, Pageable pageRequest) {
        Page<UserDTO> users = getFullUsers(predicate, pageRequest);

        return users.stream()
                // Map user to mini version and then back to remove sensitive data
                .map((miniUser) -> jpaModelMapper.mapEntity(miniUser, MiniUserDTO.class))
                .map((miniUser) -> jpaModelMapper.mapEntity(miniUser, UserDTO.class))
                .collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> new PageImpl<>(list, pageRequest, users.getTotalElements())));
    }

    /**
     * Get user by userId with full details.
     *
     * @param userId User UUID.
     * @return User with given UUID.
     */
    @Override
    public UserDTO getFullUser(UUID userId) {
        User user = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(userId.toString())));

        return jpaModelMapper.mapEntity(user, UserDTO.class);
    }

    /**
     * Get user by userId with reduced details.
     *
     * @param userId User UUID.
     * @return User with given UUID.
     */
    @Override
    public UserDTO getMiniUser(UUID userId) {
        UserDTO userDTO = getFullUser(userId);

        // Map user to mini version and then back to full remove sensitive data

        MiniUserDTO miniUserDTO = jpaModelMapper.mapEntity(userDTO, MiniUserDTO.class);

        return jpaModelMapper.mapEntity(miniUserDTO, UserDTO.class);
    }

    /**
     * Updates user by userId based on input given in UserDTO validated before by request validators.
     * Update will only be performed on field if its value is different than null.
     * User token will be revoked only if edited fields are one of the following: email, password.
     *
     * @param userId  User UUID.
     * @param userDTO Updated user.
     */
    @Override
    public void updateUser(UUID userId, UserDTO userDTO) {
        updateUserAdapter.updateUser(userId, userDTO);
    }

    /**
     * Replaces user fields by userId based on input given in UserDTO validated before by request validators.
     * All fields will be overwritten and user will be required to generate new token.
     *
     * @param userId  User UUID
     * @param userDTO Replacement user.
     */
    @Override
    public void replaceUser(UUID userId, UserDTO userDTO) {
        replaceUserAdapter.replaceUser(userId, userDTO);
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
     * Assigns predefined group to user by userId.
     *
     * @param userId        User UUID.
     * @param groupStrategy Group strategy.
     */
    @Override
    public void setUserGroup(UUID userId, GroupStrategy groupStrategy) {
        setUserGroupAdapter.setUserGroup(userId, groupStrategy);
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
        adminUser.setBirthDay(LocalDate.now().minusYears(16));
        adminUser.setEmail("admin@admin.pl");
        adminUser.setPassword("admin");
        adminUser.setGroup(new AdminGroup());

        createUser(adminUser);
    }
}
