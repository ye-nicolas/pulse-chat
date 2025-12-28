package com.nicolas.pulse.adapter.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveChatRoomMemberReq {
    @NotEmpty
    private List<@Valid @NotBlank String> memberIdList;
}
