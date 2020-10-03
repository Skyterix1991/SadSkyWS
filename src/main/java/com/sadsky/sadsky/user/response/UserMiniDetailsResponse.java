package com.sadsky.sadsky.user.response;

import com.sadsky.sadsky.user.domain.User;
import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;
import org.springframework.data.rest.core.config.Projection;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Projection(name = "miniUser", types = User.class)
public interface UserMiniDetailsResponse extends Serializable {
    UUID getUserId();

    GroupStrategy getGroup();

    LocalDateTime getCreateDate();

    LocalDateTime getUpdateDate();
}
