package com.nicolas.pulse.adapter.repository;

public class DbMeta {
    private DbMeta() {

    }

    public static final class AccountData {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SHOW_NAME = "showName";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_IS_ACTIVE = "isActive";
        public static final String COLUMN_LAST_LOGIN_AT = "lastLoginAt";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_UPDATED_BY = "updated_by";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_REMARK = "remark";
    }

    public static final class AccountRoleData {
        public static final String TABLE_NAME = "account_role";
        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_ROLE_ID = "role_id";

    }

    public static final class RoleData {
        public static final String TABLE_NAME = "role";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_UPDATED_BY = "updated_by";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_REMARK = "remark";
    }

    public static final class RolePrivilegeData {
        public static final String TABLE_NAME = "role_privilege";
        public static final String COLUMN_ROLE_ID = "role_id";
        public static final String COLUMN_PRIVILEGE = "privilege";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_CREATED_AT = "created_at";
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
