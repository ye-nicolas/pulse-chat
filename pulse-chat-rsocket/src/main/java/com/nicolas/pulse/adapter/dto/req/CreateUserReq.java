package com.nicolas.pulse.adapter.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserReq {
    @NotBlank
    private String name;
    @NotBlank
    private String showName;
    @NotBlank
    private String password;
    private String remark;
}
