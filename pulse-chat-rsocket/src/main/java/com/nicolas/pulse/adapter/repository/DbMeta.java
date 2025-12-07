package com.nicolas.pulse.adapter.repository;

public class DbMeta {
    private DbMeta() {

    }

    public static final class AccountData {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_PREFIX = TABLE_NAME + ".";
        public static final String ALIAS_PREFIX = TABLE_NAME + "_";
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
        public static final String COL_SHOW_NAME = "show_name";
        public static final String COL_PASSWORD = "password";
        public static final String COL_IS_ACTIVE = "is_active";
        public static final String COL_LAST_LOGIN_AT = "last_login_at";
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_UPDATED_BY = "updated_by";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_UPDATED_AT = "updated_at";
        public static final String COL_REMARK = "remark";
        public static final String COLUMN_ID = COLUMN_PREFIX + COL_ID;
        public static final String COLUMN_NAME = COLUMN_PREFIX + COL_NAME;
        public static final String COLUMN_SHOW_NAME = COLUMN_PREFIX + COL_SHOW_NAME;
        public static final String COLUMN_PASSWORD = COLUMN_PREFIX + COL_PASSWORD;
        public static final String COLUMN_IS_ACTIVE = COLUMN_PREFIX + COL_IS_ACTIVE;
        public static final String COLUMN_LAST_LOGIN_AT = COLUMN_PREFIX + COL_LAST_LOGIN_AT;
        public static final String COLUMN_CREATED_BY = COLUMN_PREFIX + COL_CREATED_BY;
        public static final String COLUMN_UPDATED_BY = COLUMN_PREFIX + COL_UPDATED_BY;
        public static final String COLUMN_CREATED_AT = COLUMN_PREFIX + COL_CREATED_AT;
        public static final String COLUMN_UPDATED_AT = COLUMN_PREFIX + COL_UPDATED_AT;
        public static final String COLUMN_REMARK = COLUMN_PREFIX + COL_REMARK;
        public static final String ALIAS_ID = ALIAS_PREFIX + COL_ID;
        public static final String ALIAS_NAME = ALIAS_PREFIX + COL_NAME;
        public static final String ALIAS_SHOW_NAME = ALIAS_PREFIX + COL_SHOW_NAME;
        public static final String ALIAS_PASSWORD = ALIAS_PREFIX + COL_PASSWORD;
        public static final String ALIAS_IS_ACTIVE = ALIAS_PREFIX + COL_IS_ACTIVE;
        public static final String ALIAS_LAST_LOGIN_AT = ALIAS_PREFIX + COL_LAST_LOGIN_AT;
        public static final String ALIAS_CREATED_BY = ALIAS_PREFIX + COL_CREATED_BY;
        public static final String ALIAS_UPDATED_BY = ALIAS_PREFIX + COL_UPDATED_BY;
        public static final String ALIAS_CREATED_AT = ALIAS_PREFIX + COL_CREATED_AT;
        public static final String ALIAS_UPDATED_AT = ALIAS_PREFIX + COL_UPDATED_AT;
        public static final String ALIAS_REMARK = ALIAS_PREFIX + COL_REMARK;
    }

    public static final class AccountRoleData {
        public static final String TABLE_NAME = "account_role";
        public static final String COLUMN_PREFIX = TABLE_NAME + ".";
        public static final String ALIAS_PREFIX = TABLE_NAME + "_";
        public static final String COL_ID = "id";
        public static final String COL_ACCOUNT_ID = "account_id";
        public static final String COL_ROLE_ID = "role_id";
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COLUMN_ID = COLUMN_PREFIX + COL_ID;
        public static final String COLUMN_ACCOUNT_ID = COLUMN_PREFIX + COL_ACCOUNT_ID;
        public static final String COLUMN_ROLE_ID = COLUMN_PREFIX + COL_ROLE_ID;
        public static final String COLUMN_CREATED_BY = COLUMN_PREFIX + COL_CREATED_BY;
        public static final String COLUMN_CREATED_AT = COLUMN_PREFIX + COL_CREATED_AT;
        public static final String ALIAS_ID = ALIAS_PREFIX + COL_ID;
        public static final String ALIAS_ACCOUNT_ID = ALIAS_PREFIX + COL_ACCOUNT_ID;
        public static final String ALIAS_ROLE_ID = ALIAS_PREFIX + COL_ROLE_ID;
        public static final String ALIAS_CREATED_BY = ALIAS_PREFIX + COL_CREATED_BY;
        public static final String ALIAS_CREATED_AT = ALIAS_PREFIX + COL_CREATED_AT;
    }

    public static final class RoleData {
        public static final String TABLE_NAME = "role";
        public static final String COLUMN_PREFIX = TABLE_NAME + ".";
        public static final String ALIAS_PREFIX = TABLE_NAME + "_";
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_UPDATED_BY = "updated_by";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_UPDATED_AT = "updated_at";
        public static final String COL_REMARK = "remark";
        public static final String COLUMN_ID = COLUMN_PREFIX + COL_ID;
        public static final String COLUMN_NAME = COLUMN_PREFIX + COL_NAME;
        public static final String COLUMN_CREATED_BY = COLUMN_PREFIX + COL_CREATED_BY;
        public static final String COLUMN_UPDATED_BY = COLUMN_PREFIX + COL_UPDATED_BY;
        public static final String COLUMN_CREATED_AT = COLUMN_PREFIX + COL_CREATED_AT;
        public static final String COLUMN_UPDATED_AT = COLUMN_PREFIX + COL_UPDATED_AT;
        public static final String COLUMN_REMARK = COLUMN_PREFIX + COL_REMARK;
        public static final String ALIAS_ID = ALIAS_PREFIX + COL_ID;
        public static final String ALIAS_NAME = ALIAS_PREFIX + COL_NAME;
        public static final String ALIAS_CREATED_BY = ALIAS_PREFIX + COL_CREATED_BY;
        public static final String ALIAS_UPDATED_BY = ALIAS_PREFIX + COL_UPDATED_BY;
        public static final String ALIAS_CREATED_AT = ALIAS_PREFIX + COL_CREATED_AT;
        public static final String ALIAS_UPDATED_AT = ALIAS_PREFIX + COL_UPDATED_AT;
        public static final String ALIAS_REMARK = ALIAS_PREFIX + COL_REMARK;
    }

    public static final class RolePrivilegeData {
        public static final String TABLE_NAME = "role_privilege";
        public static final String ALIAS_PREFIX = TABLE_NAME + "_";
        public static final String COLUMN_PREFIX = TABLE_NAME + ".";
        public static final String COL_ID = "id";
        public static final String COL_ROLE_ID = "role_id";
        public static final String COL_PRIVILEGE = "privilege";
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COLUMN_ID = COLUMN_PREFIX + COL_ID;
        public static final String COLUMN_ROLE_ID = COLUMN_PREFIX + COL_ROLE_ID;
        public static final String COLUMN_PRIVILEGE = COLUMN_PREFIX + COL_PRIVILEGE;
        public static final String COLUMN_CREATED_BY = COLUMN_PREFIX + COL_CREATED_BY;
        public static final String COLUMN_CREATED_AT = COLUMN_PREFIX + COL_CREATED_AT;
        public static final String ALIAS_ID = ALIAS_PREFIX + COL_ID;
        public static final String ALIAS_ROLE_ID = ALIAS_PREFIX + COL_ROLE_ID;
        public static final String ALIAS_PRIVILEGE = ALIAS_PREFIX + COL_PRIVILEGE;
        public static final String ALIAS_CREATED_BY = ALIAS_PREFIX + COL_CREATED_BY;
        public static final String ALIAS_CREATED_AT = ALIAS_PREFIX + COL_CREATED_AT;
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
        public static final String COLUMN_PREFIX = TABLE_NAME + ".";
        public static final String ALIAS_PREFIX = TABLE_NAME + "_";
        public static final String COL_ID = "id";
        public static final String COL_REQUESTER_ACCOUNT_ID = "requester_account_id";
        public static final String COL_RECIPIENT_ACCOUNT_ID = "recipient_account_id";
        public static final String COL_STATUS = "status";
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_UPDATED_BY = "updated_by";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_UPDATED_AT = "updated_at";
        public static final String COLUMN_ID = COLUMN_PREFIX + COL_ID;
        public static final String COLUMN_REQUESTER_ACCOUNT_ID = COLUMN_PREFIX + COL_REQUESTER_ACCOUNT_ID;
        public static final String COLUMN_RECIPIENT_ACCOUNT_ID = COLUMN_PREFIX + COL_RECIPIENT_ACCOUNT_ID;
        public static final String COLUMN_STATUS = COLUMN_PREFIX + COL_STATUS;
        public static final String COLUMN_CREATED_BY = COLUMN_PREFIX + COL_CREATED_BY;
        public static final String COLUMN_UPDATED_BY = COLUMN_PREFIX + COL_UPDATED_BY;
        public static final String COLUMN_CREATED_AT = COLUMN_PREFIX + COL_CREATED_AT;
        public static final String COLUMN_UPDATED_AT = COLUMN_PREFIX + COL_UPDATED_AT;
        public static final String ALIAS_ID = ALIAS_PREFIX + COL_ID;
        public static final String ALIAS_REQUESTER_ACCOUNT_ID = ALIAS_PREFIX + COL_REQUESTER_ACCOUNT_ID;
        public static final String ALIAS_RECIPIENT_ACCOUNT_ID = ALIAS_PREFIX + COL_RECIPIENT_ACCOUNT_ID;
        public static final String ALIAS_STATUS = ALIAS_PREFIX + COL_STATUS;
        public static final String ALIAS_CREATED_BY = ALIAS_PREFIX + COL_CREATED_BY;
        public static final String ALIAS_UPDATED_BY = ALIAS_PREFIX + COL_UPDATED_BY;
        public static final String ALIAS_CREATED_AT = ALIAS_PREFIX + COL_CREATED_AT;
        public static final String ALIAS_UPDATED_AT = ALIAS_PREFIX + COL_UPDATED_AT;
    }
}
