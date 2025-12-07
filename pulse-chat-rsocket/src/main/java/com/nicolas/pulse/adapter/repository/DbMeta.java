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
        public static final String COLUMN_PREFIX = TABLE_NAME + ".";
        public static final String ALIAS_PREFIX = TABLE_NAME + "_";
        public static final String COL_ID = "id";
        public static final String COL_ACCOUNT_ID = "account_id";
        public static final String COL_ROOM_ID = "chat_room_id";
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_UPDATED_BY = "updated_by";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_UPDATED_AT = "updated_at";
        public static final String COL_LAST_READ_MESSAGE_ID = "last_read_message_id";
        public static final String COL_IS_MUTED = "is_muted";
        public static final String COL_IS_PINNED = "is_pinned";
        public static final String COLUMN_ID = COLUMN_PREFIX + COL_ID;
        public static final String COLUMN_ACCOUNT_ID = COLUMN_PREFIX + COL_ACCOUNT_ID;
        public static final String COLUMN_ROOM_ID = COLUMN_PREFIX + COL_ROOM_ID;
        public static final String COLUMN_CREATED_BY = COLUMN_PREFIX + COL_CREATED_BY;
        public static final String COLUMN_UPDATED_BY = COLUMN_PREFIX + COL_UPDATED_BY;
        public static final String COLUMN_CREATED_AT = COLUMN_PREFIX + COL_CREATED_AT;
        public static final String COLUMN_UPDATED_AT = COLUMN_PREFIX + COL_UPDATED_AT;
        public static final String COLUMN_LAST_READ_MESSAGE_ID = COLUMN_PREFIX + COL_LAST_READ_MESSAGE_ID;
        public static final String COLUMN_IS_MUTED = COLUMN_PREFIX + COL_IS_MUTED;
        public static final String COLUMN_IS_PINNED = COLUMN_PREFIX + COL_IS_PINNED;
        public static final String ALIAS_ID = ALIAS_PREFIX + COL_ID;
        public static final String ALIAS_ACCOUNT_ID = ALIAS_PREFIX + COL_ACCOUNT_ID;
        public static final String ALIAS_ROOM_ID = ALIAS_PREFIX + COL_ROOM_ID;
        public static final String ALIAS_CREATED_BY = ALIAS_PREFIX + COL_CREATED_BY;
        public static final String ALIAS_UPDATED_BY = ALIAS_PREFIX + COL_UPDATED_BY;
        public static final String ALIAS_CREATED_AT = ALIAS_PREFIX + COL_CREATED_AT;
        public static final String ALIAS_UPDATED_AT = ALIAS_PREFIX + COL_UPDATED_AT;
        public static final String ALIAS_LAST_READ_MESSAGE_ID = ALIAS_PREFIX + COL_LAST_READ_MESSAGE_ID;
        public static final String ALIAS_IS_MUTED = ALIAS_PREFIX + COL_IS_MUTED;
        public static final String ALIAS_IS_PINNED = ALIAS_PREFIX + COL_IS_PINNED;
    }

    public static final class ChatRoomData {
        public static final String TABLE_NAME = "chat_room";
        public static final String COLUMN_PREFIX = TABLE_NAME + ".";
        public static final String ALIAS_PREFIX = TABLE_NAME + "_";
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
        public static final String COL_TYPE = "type";
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_UPDATED_BY = "updated_by";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_UPDATED_AT = "updated_at";
        public static final String COLUMN_ID = COLUMN_PREFIX + COL_ID;
        public static final String COLUMN_NAME = COLUMN_PREFIX + COL_NAME;
        public static final String COLUMN_TYPE = COLUMN_PREFIX + COL_TYPE;
        public static final String COLUMN_CREATED_BY = COLUMN_PREFIX + COL_CREATED_BY;
        public static final String COLUMN_UPDATED_BY = COLUMN_PREFIX + COL_UPDATED_BY;
        public static final String COLUMN_CREATED_AT = COLUMN_PREFIX + COL_CREATED_AT;
        public static final String COLUMN_UPDATED_AT = COLUMN_PREFIX + COL_UPDATED_AT;
        public static final String ALIAS_ID = ALIAS_PREFIX + COL_ID;
        public static final String ALIAS_NAME = ALIAS_PREFIX + COL_NAME;
        public static final String ALIAS_TYPE = ALIAS_PREFIX + COL_TYPE;
        public static final String ALIAS_CREATED_BY = ALIAS_PREFIX + COL_CREATED_BY;
        public static final String ALIAS_UPDATED_BY = ALIAS_PREFIX + COL_UPDATED_BY;
        public static final String ALIAS_CREATED_AT = ALIAS_PREFIX + COL_CREATED_AT;
        public static final String ALIAS_UPDATED_AT = ALIAS_PREFIX + COL_UPDATED_AT;
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

    public static final class ChatMessageData {
        public static final String TABLE_NAME = "chat_message";
        public static final String COLUMN_PREFIX = TABLE_NAME + ".";
        public static final String ALIAS_PREFIX = TABLE_NAME + "_";
        public static final String COL_ID = "id";
        public static final String COL_ROOM_ID = "room_id";
        public static final String COL_MEMBER_ID = "member_id";
        public static final String COL_TYPE = "type";
        public static final String COL_CONTENT = "content";
        public static final String COL_IS_DELETE = "is_delete";
        public static final String COL_DELETED_AT = "delete_at";
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_UPDATED_AT = "updated_at";
        public static final String COLUMN_ID = COLUMN_PREFIX + COL_ID;
        public static final String COLUMN_ROOM_ID = COLUMN_PREFIX + COL_ROOM_ID;
        public static final String COLUMN_MEMBER_ID = COLUMN_PREFIX + COL_MEMBER_ID;
        public static final String COLUMN_TYPE = COLUMN_PREFIX + COL_TYPE;
        public static final String COLUMN_CONTENT = COLUMN_PREFIX + COL_CONTENT;
        public static final String COLUMN_IS_DELETE = COLUMN_PREFIX + COL_IS_DELETE;
        public static final String COLUMN_DELETED_AT = COLUMN_PREFIX + COL_DELETED_AT;
        public static final String COLUMN_CREATED_BY = COLUMN_PREFIX + COL_CREATED_BY;
        public static final String COLUMN_CREATED_AT = COLUMN_PREFIX + COL_CREATED_AT;
        public static final String COLUMN_UPDATED_AT = COLUMN_PREFIX + COL_UPDATED_AT;
        public static final String ALIAS_ID = ALIAS_PREFIX + COL_ID;
        public static final String ALIAS_ROOM_ID = ALIAS_PREFIX + COL_ROOM_ID;
        public static final String ALIAS_MEMBER_ID = ALIAS_PREFIX + COL_MEMBER_ID;
        public static final String ALIAS_TYPE = ALIAS_PREFIX + COL_TYPE;
        public static final String ALIAS_CONTENT = ALIAS_PREFIX + COL_CONTENT;
        public static final String ALIAS_IS_DELETE = ALIAS_PREFIX + COL_IS_DELETE;
        public static final String ALIAS_DELETED_AT = ALIAS_PREFIX + COL_DELETED_AT;
        public static final String ALIAS_CREATED_BY = ALIAS_PREFIX + COL_CREATED_BY;
        public static final String ALIAS_CREATED_AT = ALIAS_PREFIX + COL_CREATED_AT;
        public static final String ALIAS_UPDATED_AT = ALIAS_PREFIX + COL_UPDATED_AT;
    }

    public static final class ChatMessageReadData {
        public static final String TABLE_NAME = "chat_message_read";
        public static final String COLUMN_PREFIX = TABLE_NAME + ".";
        public static final String ALIAS_PREFIX = TABLE_NAME + "_";
        public static final String COL_ID = "id";
        public static final String COL_MESSAGE_ID = "message_id";
        public static final String COL_ROOM_ID = "room_id";
        public static final String COL_MEMBER_ID = "member_id";
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COLUMN_ID = COLUMN_PREFIX + COL_ID;
        public static final String COLUMN_MESSAGE_ID = COLUMN_PREFIX + COL_MESSAGE_ID;
        public static final String COLUMN_ROOM_ID = COLUMN_PREFIX + COL_ROOM_ID;
        public static final String COLUMN_MEMBER_ID = COLUMN_PREFIX + COL_MEMBER_ID;
        public static final String COLUMN_CREATED_BY = COLUMN_PREFIX + COL_CREATED_BY;
        public static final String COLUMN_CREATED_AT = COLUMN_PREFIX + COL_CREATED_AT;
        public static final String ALIAS_ID = ALIAS_PREFIX + COL_ID;
        public static final String ALIAS_MESSAGE_ID = ALIAS_PREFIX + COL_MESSAGE_ID;
        public static final String ALIAS_ROOM_ID = ALIAS_PREFIX + COL_ROOM_ID;
        public static final String ALIAS_MEMBER_ID = ALIAS_PREFIX + COL_MEMBER_ID;
        public static final String ALIAS_CREATED_BY = ALIAS_PREFIX + COL_CREATED_BY;
        public static final String ALIAS_CREATED_AT = ALIAS_PREFIX + COL_CREATED_AT;
    }
}
