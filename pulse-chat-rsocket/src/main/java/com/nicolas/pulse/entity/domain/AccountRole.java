package com.nicolas.pulse.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRole {
    private String id;
    private String accountId;
    private Role role;
    private String createdBy;
    private OffsetDateTime createdAt;
}
