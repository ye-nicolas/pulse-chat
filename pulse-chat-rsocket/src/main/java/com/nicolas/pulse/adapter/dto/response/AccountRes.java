package com.nicolas.pulse.adapter.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRes {
    private String id;
    private String name;
    private String showName;
    private boolean isActive;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private ZonedDateTime lastLoginAt;
    private String createdBy;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private ZonedDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private ZonedDateTime updatedAt;
    private String remark;
}
