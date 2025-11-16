package com.nicolas.pulse.adapter.repository;

public class DbMeta {
    private DbMeta() {

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
