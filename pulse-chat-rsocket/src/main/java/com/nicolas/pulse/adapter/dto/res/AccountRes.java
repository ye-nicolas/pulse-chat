package com.nicolas.pulse.adapter.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRes {
    private String id;
    private String name;
    private String showName;
    private boolean isActive;
    private String createdBy;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private Instant createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private Instant updatedAt;
    private String remark;
}
