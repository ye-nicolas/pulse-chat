package com.nicolas.pulse.adapter.dto.req;

import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddChatMessageReq {
    @NotNull
    private String roomId;
    @NotNull
    private ChatMessageType type;
    @NotEmpty
    private String content;
}
