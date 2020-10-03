package com.sadsky.sadsky.user.domain;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import com.sadsky.sadsky.user.domain.group.Permission;
import com.sadsky.sadsky.user.domain.group.Permissions;
import com.sadsky.sadsky.user.domain.group.SelfPermission;
import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;
import com.sadsky.sadsky.user.domain.group.strategy.UserGroup;
import com.sadsky.sadsky.user.response.UserMiniDetailsResponse;
import com.sadsky.sadsky.util.annotation.SortBlacklisted;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class User implements UserMiniDetailsResponse {

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

    @Column(nullable = false)
    private LocalDate birthDay;

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

    @PrePersist
    protected void onCreate() {
        if (group == null) group = new UserGroup();

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
