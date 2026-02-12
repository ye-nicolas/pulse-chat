package com.nicolas.util;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.chat.room.member.ChatRoomMemberData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ChatRoomMemberDataMapping {
    public static final Map<String, Function<ChatRoomMemberData, ?>> MAPPING = new LinkedHashMap<>();

    static {
        MAPPING.put(DbMeta.ChatRoomMemberData.COL_ID, ChatRoomMemberData::getId);
        MAPPING.put(DbMeta.ChatRoomMemberData.COL_ACCOUNT_ID, ChatRoomMemberData::getAccountId);
        MAPPING.put(DbMeta.ChatRoomMemberData.COL_ROOM_ID, ChatRoomMemberData::getRoomId);
        MAPPING.put(DbMeta.ChatRoomMemberData.COL_IS_MUTED, ChatRoomMemberData::isMuted);
        MAPPING.put(DbMeta.ChatRoomMemberData.COL_IS_PINNED, ChatRoomMemberData::isPinned);
        MAPPING.put(DbMeta.ChatRoomMemberData.COL_CREATED_BY, ChatRoomMemberData::getCreatedBy);
        MAPPING.put(DbMeta.ChatRoomMemberData.COL_UPDATED_BY, ChatRoomMemberData::getUpdatedBy);
        MAPPING.put(DbMeta.ChatRoomMemberData.COL_CREATED_AT, ChatRoomMemberData::getCreatedAt);
        MAPPING.put(DbMeta.ChatRoomMemberData.COL_UPDATED_AT, ChatRoomMemberData::getUpdatedAt);
    }
}
