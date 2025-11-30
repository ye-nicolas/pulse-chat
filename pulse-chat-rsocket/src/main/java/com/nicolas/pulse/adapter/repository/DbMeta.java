package com.nicolas.pulse.adapter.repository;

public class DbMeta {
    private DbMeta() {

    }

    public static final class AccountData {
        public static final String TABLE_NAME = "account";
        public static final String PREFIX = TABLE_NAME + "_";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SHOW_NAME = "show_name";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_IS_ACTIVE = "is_active";
        public static final String COLUMN_LAST_LOGIN_AT = "last_login_at";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_UPDATED_BY = "updated_by";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_REMARK = "remark";
        public static final String ALIAS_ID = PREFIX + COLUMN_ID;
        public static final String ALIAS_NAME = PREFIX + COLUMN_NAME;
        public static final String ALIAS_SHOW_NAME = PREFIX + COLUMN_SHOW_NAME;
        public static final String ALIAS_PASSWORD = PREFIX + COLUMN_PASSWORD;
        public static final String ALIAS_IS_ACTIVE = PREFIX + COLUMN_IS_ACTIVE;
        public static final String ALIAS_LAST_LOGIN_AT = PREFIX + COLUMN_LAST_LOGIN_AT;
        public static final String ALIAS_CREATED_BY = PREFIX + COLUMN_CREATED_BY;
        public static final String ALIAS_UPDATED_BY = PREFIX + COLUMN_UPDATED_BY;
        public static final String ALIAS_CREATED_AT = PREFIX + COLUMN_CREATED_AT;
        public static final String ALIAS_UPDATED_AT = PREFIX + COLUMN_UPDATED_AT;
        public static final String ALIAS_REMARK = PREFIX + COLUMN_REMARK;
    }

    public static final class AccountRoleData {
        public static final String TABLE_NAME = "account_role";
        public static final String PREFIX = TABLE_NAME + "_";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_ROLE_ID = "role_id";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String ALIAS_ID = PREFIX + COLUMN_ID;
        public static final String ALIAS_ACCOUNT_ID = PREFIX + COLUMN_ACCOUNT_ID;
        public static final String ALIAS_ROLE_ID = PREFIX + COLUMN_ROLE_ID;
        public static final String ALIAS_CREATED_BY = PREFIX + COLUMN_CREATED_BY;
        public static final String ALIAS_CREATED_AT = PREFIX + COLUMN_CREATED_AT;
    }

    public static final class RoleData {
        public static final String TABLE_NAME = "role";
        public static final String PREFIX = TABLE_NAME + "_";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_UPDATED_BY = "updated_by";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_REMARK = "remark";
        public static final String ALIAS_ID = PREFIX + COLUMN_ID;
        public static final String ALIAS_NAME = PREFIX + COLUMN_NAME;
        public static final String ALIAS_CREATED_BY = PREFIX + COLUMN_CREATED_BY;
        public static final String ALIAS_UPDATED_BY = PREFIX + COLUMN_UPDATED_BY;
        public static final String ALIAS_CREATED_AT = PREFIX + COLUMN_CREATED_AT;
        public static final String ALIAS_UPDATED_AT = PREFIX + COLUMN_UPDATED_AT;
        public static final String ALIAS_REMARK = PREFIX + COLUMN_REMARK;
    }

    public static final class RolePrivilegeData {
        public static final String TABLE_NAME = "role_privilege";
        public static final String PREFIX = TABLE_NAME + "_";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ROLE_ID = "role_id";
        public static final String COLUMN_PRIVILEGE = "privilege";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String ALIAS_ID = PREFIX + COLUMN_ID;
        public static final String ALIAS_ROLE_ID = PREFIX + COLUMN_ROLE_ID;
        public static final String ALIAS_PRIVILEGE = PREFIX + COLUMN_PRIVILEGE;
        public static final String ALIAS_CREATED_BY = PREFIX + COLUMN_CREATED_BY;
        public static final String ALIAS_CREATED_AT = PREFIX + COLUMN_CREATED_AT;
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

    public static final class FriendShipData {
        public static final String TABLE_NAME = "friend_ship";
        public static final String PREFIX = TABLE_NAME + "_";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_REQUESTER_ACCOUNT_ID = "requester_account_id";
        public static final String COLUMN_RECIPIENT_ACCOUNT_ID = "recipient_account_id";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_CREATED_BY = "created_by";
        public static final String COLUMN_UPDATED_BY = "updated_by";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String ALIAS_ID = PREFIX + COLUMN_ID;
        public static final String ALIAS_REQUESTER_ACCOUNT_ID = PREFIX + COLUMN_REQUESTER_ACCOUNT_ID;
        public static final String ALIAS_RECIPIENT_ACCOUNT_ID = PREFIX + COLUMN_RECIPIENT_ACCOUNT_ID;
        public static final String ALIAS_STATUS = PREFIX + COLUMN_STATUS;
        public static final String ALIAS_CREATED_BY = PREFIX + COLUMN_CREATED_BY;
        public static final String ALIAS_UPDATED_BY = PREFIX + COLUMN_UPDATED_BY;
        public static final String ALIAS_CREATED_AT = PREFIX + COLUMN_CREATED_AT;
        public static final String ALIAS_UPDATED_AT = PREFIX + COLUMN_UPDATED_AT;
    }
}
