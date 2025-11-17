package com.nicolas.pulse.adapter.repository;

public class DbMeta {
    private DbMeta() {

    }
    public static final class ChatRoomMemberData {
        public static final String TABLE_NAME = "chat_room_member";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_ROOM_ID = "chat_room_id";
        public static final String COLUMN_ROLE = "role";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_UPDATED_BY = "updated_by";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_LAST_READ_MESSAGE_ID = "last_read_message_id";
        public static final String COLUMN_IS_MUTED = "is_muted";
        public static final String COLUMN_IS_PINNED = "is_pinned";
    }
    
    public static final class ChatRoomData {
        public static final String TABLE_NAME = "chat_room";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_UPDATED_BY = "updated_by";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }
}
