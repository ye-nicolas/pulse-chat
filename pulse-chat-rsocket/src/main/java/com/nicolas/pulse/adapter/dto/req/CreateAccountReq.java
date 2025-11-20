package com.nicolas.pulse.adapter.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountReq {
    @NotBlank
    private String name;
    @NotBlank
    private String showName;
    @NotBlank
    private String password;
    @NotEmpty
    private Set<@Valid @NotBlank String> roleIdSet;
    private String remark;
}
