package com.nicolas.pulse.adapter.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRes {
    private String id;
    private String roomId;
    private ChatMessageType type;
    private String content;
    private String createBy;
    private Instant createAt;
    private boolean isDelete;
}
