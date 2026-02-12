package com.nicolas.util;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.chat.message.ChatMessageData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ChatMessageDataMapping {
    public static final Map<String, Function<ChatMessageData, ?>> MAPPING = new LinkedHashMap<>();

    static {
        MAPPING.put(DbMeta.ChatMessageData.COL_ID, ChatMessageData::getId);
        MAPPING.put(DbMeta.ChatMessageData.COL_ROOM_ID, ChatMessageData::getRoomId);
        MAPPING.put(DbMeta.ChatMessageData.COL_MEMBER_ID, ChatMessageData::getMemberId);
        MAPPING.put(DbMeta.ChatMessageData.COL_TYPE, ChatMessageData::getType);
        MAPPING.put(DbMeta.ChatMessageData.COL_CONTENT, ChatMessageData::getContent);
        MAPPING.put(DbMeta.ChatMessageData.COL_IS_DELETE, ChatMessageData::isDelete);
        MAPPING.put(DbMeta.ChatMessageData.COL_CREATED_BY, ChatMessageData::getCreatedBy);
        MAPPING.put(DbMeta.ChatMessageData.COL_CREATED_AT, ChatMessageData::getCreatedAt);
        MAPPING.put(DbMeta.ChatMessageData.COL_UPDATED_AT, ChatMessageData::getUpdatedAt);
    }
}
