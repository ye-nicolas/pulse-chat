package com.nicolas.util;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.chat.room.ChatRoomData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ChatRoomDataMapping {
    public static final Map<String, Function<ChatRoomData, ?>> MAPPING = new LinkedHashMap<>();

    static {
        MAPPING.put(DbMeta.ChatRoomData.COL_ID, ChatRoomData::getId);
        MAPPING.put(DbMeta.ChatRoomData.COL_NAME, ChatRoomData::getName);
        MAPPING.put(DbMeta.ChatRoomData.COL_CREATED_BY, ChatRoomData::getCreatedBy);
        MAPPING.put(DbMeta.ChatRoomData.COL_UPDATED_BY, ChatRoomData::getUpdatedBy);
        MAPPING.put(DbMeta.ChatRoomData.COL_CREATED_AT, ChatRoomData::getCreatedAt);
        MAPPING.put(DbMeta.ChatRoomData.COL_UPDATED_AT, ChatRoomData::getUpdatedAt);
    }
}
