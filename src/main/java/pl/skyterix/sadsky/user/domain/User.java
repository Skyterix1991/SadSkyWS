package pl.skyterix.sadsky.user.domain;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import pl.skyterix.sadsky.prediction.domain.Prediction;
import pl.skyterix.sadsky.user.domain.group.Permission;
import pl.skyterix.sadsky.user.domain.group.Permissions;
import pl.skyterix.sadsky.user.domain.group.SelfPermission;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;
import pl.skyterix.sadsky.user.domain.group.strategy.UserGroup;
import pl.skyterix.sadsky.util.annotation.SortBlacklisted;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class User {

    public final static int DEFAULT_WAKE_HOUR = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SortBlacklisted
    @QueryType(PropertyType.NONE)
    private long id;

    @NaturalId
    @Column(nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "friend_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @SortBlacklisted
    private List<User> friends;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "user_friend_pending_invites",
            joinColumns = @JoinColumn(name = "friend_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @SortBlacklisted
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> friendPendingInvites;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "user_friends_to",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @SortBlacklisted
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> friendsTo;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "user_friend_sent_invites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @SortBlacklisted
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> friendSentInvites;

    @OneToMany(cascade = CascadeType.ALL)
    @SortBlacklisted
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Prediction> predictions;

    @Column(nullable = false)
    private LocalDate birthDay;

    @Column(nullable = false)
    private int wakeHour;

    @Column(nullable = false)
    @SortBlacklisted
    @QueryType(PropertyType.NONE)
    private String email;

    @Column(nullable = false)
    @SortBlacklisted
    @QueryType(PropertyType.NONE)
    private String encryptedPassword;

    @Column(name = "usergroup", nullable = false)
    private GroupStrategy group;

    @Column(nullable = false)
    private LocalDateTime lastTokenRevokeDate;

    @Column(updatable = false)
    private LocalDateTime createDate;

    @Column
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;

    public User() {
        this.group = new UserGroup();
        this.predictions = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.friendsTo = new ArrayList<>();
        this.friendPendingInvites = new ArrayList<>();
        this.friendSentInvites = new ArrayList<>();

        this.wakeHour = DEFAULT_WAKE_HOUR;
    }

    @PrePersist
    protected void onCreate() {
        this.userId = UUID.randomUUID();

        this.createDate = LocalDateTime.now();
        this.lastTokenRevokeDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    public boolean hasPermission(UUID targetUserId, SelfPermission selfPermission, Permission permission) {
        if (group == null) group = new UserGroup();

        if (userId.equals(targetUserId))
            return group.hasPermission(selfPermission);
        else
            return group.hasPermission(permission);
    }

    public boolean hasPermission(Permissions permission) {
        if (group == null) group = new UserGroup();

        return group.hasPermission(permission);
    }
}
