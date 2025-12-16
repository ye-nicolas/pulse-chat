package com.nicolas.pulse.adapter.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRoomReq {
    private String roomName;
    private Set<String> accountIdSet;
}
