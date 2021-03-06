package pl.skyterix.sadsky.user.domain;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.skyterix.sadsky.exception.Errors;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;
import pl.skyterix.sadsky.prediction.domain.day.domain.dto.DayDTO;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;
import pl.skyterix.sadsky.user.domain.dto.MiniUserDTO;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.user.domain.group.strategy.AdminGroup;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Skyte
 */
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
    private final AcceptUserPendingInvitePort acceptUserPendingInviteAdapter;
    private final RefuseUserPendingInvitePort refuseUserPendingInviteAdapter;
    private final AddUserToFriendsToPort addUserToFriendsToAdapter;
    private final RemoveUserFromFriendsToPort removeUserFromFriendsToAdapter;
    private final RemoveUserFromFriendsPort removeUserFromFriendsAdapter;
    private final CancelSentInvitePort cancelSentInviteAdapter;

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
     * Get user mini friends.
     * Friends are users that can view current user predictions.
     *
     * @param userId User UUID.
     * @return List of users.
     */
    @Override
    public List<UserDTO> getUserMiniFriends(UUID userId) {
        List<UserDTO> friends = getUserFullFriends(userId);

        return friends.stream()
                // Initialize lazy list
                .peek(user -> Hibernate.initialize(user.getFriends()))
                .map((user) -> jpaModelMapper.mapEntity(user, MiniUserDTO.class))
                .map((user) -> jpaModelMapper.mapEntity(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get user mini friends to.
     * Friends are users that current user can view predictions of.
     *
     * @param userId User UUID.
     * @return List of users.
     */
    @Override
    public List<UserDTO> getUserMiniFriendsTo(UUID userId) {
        List<UserDTO> friends = getUserFullFriendsTo(userId);

        return friends.stream()
                // Initialize lazy list
                .peek(user -> Hibernate.initialize(user.getFriendsTo()))
                .map((user) -> jpaModelMapper.mapEntity(user, MiniUserDTO.class))
                .map((user) -> jpaModelMapper.mapEntity(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get user mini pending invites.
     * Pending invites are invites received from other users.
     *
     * @param userId User UUID.
     * @return List of users.
     */
    @Override
    public List<UserDTO> getUserMiniPendingInvites(UUID userId) {
        List<UserDTO> friends = getUserFullPendingInvites(userId);

        return friends.stream()
                // Initialize lazy list
                .peek(user -> Hibernate.initialize(user.getFriendPendingInvites()))
                .map((user) -> jpaModelMapper.mapEntity(user, MiniUserDTO.class))
                .map((user) -> jpaModelMapper.mapEntity(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get user mini sent invites.
     * Sent invites are invites sent from user to other users.
     *
     * @param userId User UUID.
     * @return List of users.
     */
    @Override
    public List<UserDTO> getUserMiniSentInvites(UUID userId) {
        List<UserDTO> friends = getUserFullSentInvites(userId);

        return friends.stream()
                // Initialize lazy list
                .peek(user -> Hibernate.initialize(user.getFriendSentInvites()))
                .map((user) -> jpaModelMapper.mapEntity(user, MiniUserDTO.class))
                .map((user) -> jpaModelMapper.mapEntity(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get user full friends.
     * Friends are users that can view current user predictions.
     *
     * @param userId User UUID.
     * @return List of users.
     */
    @Override
    public List<UserDTO> getUserFullFriends(UUID userId) {
        List<User> friends = userRepository.findAllUserFriends(userId);

        return friends.stream()
                // Initialize lazy list
                .peek(user -> Hibernate.initialize(user.getFriends()))
                .map((user) -> jpaModelMapper.mapEntity(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get user full friends to.
     * Friends to are users that current user can view predictions of.
     *
     * @param userId User UUID.
     * @return List of users.
     */
    @Override
    public List<UserDTO> getUserFullFriendsTo(UUID userId) {
        List<User> friends = userRepository.findAllUserFriendsTo(userId);

        return friends.stream()
                // Initialize lazy list
                .peek(user -> Hibernate.initialize(user.getFriendsTo()))
                .map((user) -> jpaModelMapper.mapEntity(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get user full pending invites.
     * Pending invites are invites received from other users.
     *
     * @param userId User UUID.
     * @return List of users.
     */
    @Override
    public List<UserDTO> getUserFullPendingInvites(UUID userId) {
        List<User> friends = userRepository.findAllUserPendingInvites(userId);

        return friends.stream()
                // Initialize lazy list
                .peek(user -> Hibernate.initialize(user.getFriendPendingInvites()))
                .map((user) -> jpaModelMapper.mapEntity(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get user full sent invites.
     * Sent invites are invites sent from user to other users.
     *
     * @param userId User UUID.
     * @return List of users.
     */
    @Override
    public List<UserDTO> getUserFullSentInvites(UUID userId) {
        List<User> friends = userRepository.findAllUserSentInvites(userId);

        return friends.stream()
                // Initialize lazy list
                .peek(user -> Hibernate.initialize(user.getFriendSentInvites()))
                .map((user) -> jpaModelMapper.mapEntity(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Add user to friends to.
     *
     * @param userId   User UUID.
     * @param friendId Friend UUID.
     */
    @Override
    public void addUserToFriendsTo(UUID userId, UUID friendId) {
        addUserToFriendsToAdapter.addUserToFriendsTo(userId, friendId);
    }

    /**
     * Remove user from friends.
     * Friends are users that can view current user predictions.
     *
     * @param userId   User UUID.
     * @param friendId Friend UUID.
     */
    @Override
    public void removeUserFromFriends(UUID userId, UUID friendId) {
        removeUserFromFriendsAdapter.removeUserFromFriends(userId, friendId);
    }


    /**
     * Remove user from friends to.
     * Friends to are users that current user can view predictions of.
     *
     * @param userId   User UUID.
     * @param friendId Friend UUID.
     */
    @Override
    public void removeUserFromFriendsTo(UUID userId, UUID friendId) {
        removeUserFromFriendsToAdapter.removeUserFromFriendsTo(userId, friendId);
    }

    /**
     * Accept user pending invite.
     * Pending invites are invites received from other users.
     *
     * @param userId   User UUID.
     * @param friendId Friend UUID.
     */
    @Override
    public void acceptUserPendingInvite(UUID userId, UUID friendId) {
        acceptUserPendingInviteAdapter.acceptUserPendingInvite(userId, friendId);
    }

    /**
     * Refuse user pending invite.
     * Pending invites are invites received from other users.
     *
     * @param userId   User UUID.
     * @param friendId Friend UUID.
     */
    @Override
    public void refuseUserPendingInvite(UUID userId, UUID friendId) {
        refuseUserPendingInviteAdapter.refuseUserPendingInvite(userId, friendId);
    }

    /**
     * Cancel sent invite.
     * Sent invites are invites sent from user to other users.
     *
     * @param userId   User UUID.
     * @param friendId Friend UUID.
     */
    @Override
    public void cancelSentInvite(UUID userId, UUID friendId) {
        cancelSentInviteAdapter.cancelSentInvite(userId, friendId);
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
                .filter("dev"::equals)
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
        adminUser.setEncryptedPassword(new BCryptPasswordEncoder().encode("admin"));
        adminUser.setGroup(new AdminGroup());
        adminUser.setWakeHour(12);

        PredictionDTO predictionDTO = new PredictionDTO();
        predictionDTO.setOwner(adminUser);

        // Generate fully filled days
        List<DayDTO> days = IntStream.range(1, 8)
                .mapToObj(dayNumber -> {
                    DayDTO dayDTO = new DayDTO();
                    dayDTO.setDayNumber(dayNumber);

                    return dayDTO;
                })
                .peek(day -> {
                    Set<Emotion> emotions = new HashSet<>();
                    emotions.add(Emotion.ACTIVE);
                    emotions.add(Emotion.DISHEARTENED);
                    emotions.add(Emotion.HELPLESS);
                    emotions.add(Emotion.HOLLOW);
                    emotions.add(Emotion.BRUNT);
                    emotions.add(Emotion.LONELY);

                    day.setMorningEmotions(emotions);
                    day.setAfternoonEmotions(emotions);
                    day.setEveningEmotions(emotions);
                }).collect(Collectors.toList());

        predictionDTO.setDays(days);

        adminUser.setPredictions(Collections.singletonList(predictionDTO));

        userRepository.save(jpaModelMapper.mapEntity(adminUser, User.class));
    }
}
