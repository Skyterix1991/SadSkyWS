package pl.skyterix.sadsky.user.response;

import org.springframework.data.rest.core.config.Projection;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Projection(name = "miniUser", types = User.class)
public interface UserMiniDetailsResponse extends Serializable {
    UUID getUserId();

    String getFirstName();

    String getLastName();

    GroupStrategy getGroup();

    LocalDateTime getCreateDate();

    LocalDateTime getUpdateDate();
}
