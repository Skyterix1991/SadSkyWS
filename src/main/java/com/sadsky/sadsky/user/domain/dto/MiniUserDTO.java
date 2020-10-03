package com.sadsky.sadsky.user.domain.dto;

import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MiniUserDTO {

    private long id;

    private UUID userId;

    private GroupStrategy group;

    private LocalDateTime createDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LocalDateTime updateDate;
}
