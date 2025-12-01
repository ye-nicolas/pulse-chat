package com.nicolas.pulse.adapter.dto.req;

import com.nicolas.pulse.entity.enumerate.ChatRoomType;
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
    private ChatRoomType roomType;
    private Set<String> accountIdSet;
}
