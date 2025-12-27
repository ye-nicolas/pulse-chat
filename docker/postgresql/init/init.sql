CREATE SCHEMA IF NOT EXISTS pulse_chat

-- account
CREATE TABLE pulse_chat.account (
	id bpchar(26) NOT NULL,
	"name" varchar NOT NULL,
	show_name varchar NOT NULL,
	"password" varchar NOT NULL,
	is_active bool NOT NULL,
	last_login_at timestamptz NULL,
	created_by bpchar(26) NOT NULL,
	updated_by bpchar(26) NOT NULL,
	created_at timestamptz NOT NULL DEFAULT now(),
	updated_at timestamptz NOT NULL DEFAULT now(),
	remark text NULL,
	CONSTRAINT user_pk PRIMARY KEY (id),
	CONSTRAINT user_uk UNIQUE ("name")
);

-- friend_ship
CREATE TABLE pulse_chat.friend_ship (
	id bpchar(26) NOT NULL,
	requester_account_id bpchar(26) NOT NULL,
	recipient_account_id bpchar(26) NOT NULL,
	"status" varchar NOT NULL DEFAULT 'PENDING'::character varying,
	created_by bpchar(26) NOT NULL,
	updated_by bpchar(26) NOT NULL,
	created_at timestamptz NOT NULL DEFAULT now(),
	updated_at timestamptz NOT NULL DEFAULT now(),
	CONSTRAINT friend_ship_pk PRIMARY KEY (id),
	CONSTRAINT friend_ship_uk_1 UNIQUE (requester_account_id, recipient_account_id),
	CONSTRAINT friend_ship_uk_2 UNIQUE (recipient_account_id, requester_account_id)
);

-- chat_room
CREATE TABLE pulse_chat.chat_room (
	id bpchar(26) NOT NULL,
	"name" varchar NOT NULL,
	created_by bpchar(26) NOT NULL,
	updated_by bpchar(26) NOT NULL,
	created_at timestamptz NOT NULL DEFAULT now(),
	updated_at timestamptz NOT NULL DEFAULT now(),
	CONSTRAINT chat_room_pk PRIMARY KEY (id)
);

-- chat_room_member
CREATE TABLE pulse_chat.chat_room_member (
	id bpchar(26) NOT NULL,
	account_id bpchar(26) NOT NULL,
	room_id bpchar(26) NOT NULL,
	created_by bpchar(26) NOT NULL,
	updated_by bpchar(26) NOT NULL,
	created_at timestamptz NOT NULL DEFAULT now(),
	is_muted bool NOT NULL DEFAULT false,
	is_pinned bool NOT NULL DEFAULT false,
	updated_at timestamptz NOT NULL DEFAULT now(),
	CONSTRAINT chat_room_member_pk PRIMARY KEY (id),
	CONSTRAINT chat_room_member_uk UNIQUE (account_id, room_id)
);

-- chat_message
CREATE TABLE pulse_chat.chat_message (
	id bpchar(26) NOT NULL,
	room_id bpchar(26) NOT NULL,
	member_id bpchar(26) NOT NULL,
	"type" varchar NOT NULL DEFAULT 'TEXT'::character varying,
	"content" text NOT NULL,
	is_delete bool NOT NULL DEFAULT false,
	created_by bpchar(26) NOT NULL,
	updated_by bpchar(26) NOT NULL,
	created_at timestamptz NOT NULL DEFAULT now(),
	is_muted bool NOT NULL DEFAULT false,
	is_pinned bool NOT NULL DEFAULT false,
	CONSTRAINT chat_message_pk PRIMARY KEY (id)
);

-- chat_message_last_read
CREATE TABLE pulse_chat.chat_message_last_read (
	id bpchar(26) NOT NULL,
	last_message_id bpchar(26) NOT NULL,
	room_id bpchar(26) NOT NULL,
	member_id bpchar(26) NOT NULL,
	created_by bpchar(26) NOT NULL,
	created_at timestamptz NOT NULL DEFAULT now(),
	updated_at timestamptz NOT NULL DEFAULT now(),
	CONSTRAINT chat_message_last_read_pk PRIMARY KEY (id),
	CONSTRAINT chat_message_last_read_un UNIQUE (last_message_id, room_id, member_id)
);