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
public class Account {
    private String id;
    private String name;
    private String showName;
    private String password;
    private boolean isActive;
    private OffsetDateTime lastLoginAt;
    private String createdBy;
    private String updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String remark;
}
