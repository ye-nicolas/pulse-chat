package com.nicolas.pulse.adapter.repository.chat.room.member;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.chat.room.ChatRoomData;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.enumerate.ChatRoomMemberRole;
import com.nicolas.pulse.entity.enumerate.ChatRoomType;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Repository
public class ChatRoomMemberRepositoryImpl implements ChatRoomMemberRepository {
    private final ChatRoomMemberDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;
    private static final String ROOM_PREFIX = "room_";
    private static final String BASIC_SQL = """
             SELECT m.*,
                   r.%s AS %s,
                   r.%s AS %s,
                   r.%s AS %s,
                   r.%s AS %s,
                   r.%s AS %s,
                   r.%s AS %s,
                   r.%s AS %s
            FROM %s m
            JOIN %s r ON m.%s = r.%s
            """.formatted(
            DbMeta.ChatRoomData.COLUMN_ID, ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_ID,
            DbMeta.ChatRoomData.COLUMN_NAME, ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_NAME,
            DbMeta.ChatRoomData.COLUMN_TYPE, ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_TYPE,
            DbMeta.ChatRoomData.COLUMN_CREATED_BY, ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_CREATED_BY,
            DbMeta.ChatRoomData.COLUMN_UPDATED_BY, ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_UPDATED_BY,
            DbMeta.ChatRoomData.COLUMN_CREATED_AT, ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_CREATED_AT,
            DbMeta.ChatRoomData.COLUMN_UPDATED_AT, ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_UPDATED_AT,
            DbMeta.ChatRoomMemberData.TABLE_NAME,
            DbMeta.ChatRoomData.TABLE_NAME,
            DbMeta.ChatRoomMemberData.COLUMN_ROOM_ID,
            DbMeta.ChatRoomData.COLUMN_ID);
    private static final String FIND_BY_ID_SQL = BASIC_SQL + "where m.%s = $1".formatted(DbMeta.ChatRoomMemberData.COLUMN_ID);
    private static final String FIND_BY_ACCOUNT_ID_SQL = BASIC_SQL + "where m.%s = $1".formatted(DbMeta.ChatRoomMemberData.COLUMN_ACCOUNT_ID);
    private static final String FIND_BY_ROOM_ID_SQL = BASIC_SQL + "where m.%s = $1".formatted(DbMeta.ChatRoomMemberData.COLUMN_ROOM_ID);

    public ChatRoomMemberRepositoryImpl(ChatRoomMemberDataRepositoryPeer peer,
                                        R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    @Override
    public Mono<ChatRoomMember> findById(String id) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ID_SQL)
                .bind(1, id)
                .fetch()
                .one()
                .map(this::mapToData)
                .map(ChatRoomMemberDataMapper::dataToDomain);
    }

    @Override
    public Flux<ChatRoomMember> findByAccountId(String accountId) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ACCOUNT_ID_SQL)
                .bind(1, accountId)
                .fetch()
                .all()
                .map(this::mapToData)
                .map(ChatRoomMemberDataMapper::dataToDomain);
    }

    @Override
    public Flux<ChatRoomMember> findByRoomId(String roomId) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ROOM_ID_SQL)
                .bind(1, roomId)
                .fetch()
                .all()
                .map(this::mapToData)
                .map(ChatRoomMemberDataMapper::dataToDomain);
    }

    @Override
    public Mono<ChatRoomMember> save(ChatRoomMember chatRoomMember) {
        ChatRoomMemberData chatRoomMemberData = ChatRoomMemberDataMapper.domainToData(chatRoomMember);
        return peer.save(chatRoomMemberData).map(ChatRoomMemberDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(id);
    }

    @Override
    public Mono<Void> deleteByAccountId(String accountId) {
        return peer.deleteByAccountId(accountId);
    }

    @Override
    public Mono<Boolean> existsByAccountIdAndRoomId(String accountId, String roomId) {
        return peer.existsByAccountIdAndRoomId(accountId, roomId);
    }

    private ChatRoomMemberData mapToData(Map<String, Object> map) {
        return ChatRoomMemberData
                .builder()
                .id(map.get(DbMeta.ChatRoomMemberData.COLUMN_ID).toString())
                .accountId(map.get(DbMeta.ChatRoomMemberData.COLUMN_ACCOUNT_ID).toString())
                .roomId(map.get(DbMeta.ChatRoomMemberData.COLUMN_ROOM_ID).toString())
                .role(ChatRoomMemberRole.valueOf(map.get(DbMeta.ChatRoomMemberData.COLUMN_ROLE).toString()))
                .createdBy(map.get(DbMeta.ChatRoomMemberData.COLUMN_CREATED_BY).toString())
                .updatedBy(map.get(DbMeta.ChatRoomMemberData.COLUMN_UPDATED_BY).toString())
                .createdAt((Instant) map.get(DbMeta.ChatRoomMemberData.COLUMN_CREATED_AT))
                .updatedAt((Instant) map.get(DbMeta.ChatRoomMemberData.COLUMN_UPDATED_AT))
                .lastReadMessageId(map.get(DbMeta.ChatRoomMemberData.COLUMN_LAST_READ_MESSAGE_ID).toString())
                .isMuted((Boolean) map.get(DbMeta.ChatRoomMemberData.COLUMN_IS_MUTED))
                .isPinned((Boolean) map.get(DbMeta.ChatRoomMemberData.COLUMN_IS_PINNED))
                .roomData(ChatRoomData.builder()
                        .id(map.get(ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_ID).toString())
                        .name(map.get(ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_NAME).toString())
                        .type(ChatRoomType.valueOf(map.get(ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_TYPE).toString()))
                        .createdBy(map.get(ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_CREATED_BY).toString())
                        .updatedBy(map.get(ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_UPDATED_BY).toString())
                        .createdAt((Instant) map.get(ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_CREATED_AT))
                        .updatedAt((Instant) map.get(ROOM_PREFIX + DbMeta.ChatRoomData.COLUMN_UPDATED_AT))
                        .build())
                .build();
    }
}
