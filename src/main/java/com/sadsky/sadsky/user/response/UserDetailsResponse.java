package com.sadsky.sadsky.user.response;

import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDetailsResponse extends RepresentationModel<UserDetailsResponse> implements Serializable {

    private UUID userId;

    private String email;

    private GroupStrategy group;

    private LocalDateTime lastTokenRevokeDate;

    private LocalDateTime updateDate;

    private LocalDateTime createDate;

}