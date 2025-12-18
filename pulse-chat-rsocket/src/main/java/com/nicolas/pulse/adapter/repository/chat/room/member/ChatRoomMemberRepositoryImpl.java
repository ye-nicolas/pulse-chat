package com.nicolas.pulse.adapter.repository.chat.room.member;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.chat.room.ChatRoomData;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public class ChatRoomMemberRepositoryImpl implements ChatRoomMemberRepository {
    private final ChatRoomMemberDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;
    private static final String BASIC_SQL = """
             SELECT
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s,
                   %s AS %s
            FROM %s
            JOIN %s ON %s = %s
            """.formatted(
            DbMeta.ChatRoomMemberData.COLUMN_ID, DbMeta.ChatRoomMemberData.ALIAS_ID,
            DbMeta.ChatRoomMemberData.COLUMN_ACCOUNT_ID, DbMeta.ChatRoomMemberData.ALIAS_ACCOUNT_ID,
            DbMeta.ChatRoomMemberData.COLUMN_ROOM_ID, DbMeta.ChatRoomMemberData.ALIAS_ROOM_ID,
            DbMeta.ChatRoomMemberData.COLUMN_CREATED_BY, DbMeta.ChatRoomMemberData.ALIAS_CREATED_BY,
            DbMeta.ChatRoomMemberData.COLUMN_UPDATED_BY, DbMeta.ChatRoomMemberData.ALIAS_UPDATED_BY,
            DbMeta.ChatRoomMemberData.COLUMN_CREATED_AT, DbMeta.ChatRoomMemberData.ALIAS_CREATED_AT,
            DbMeta.ChatRoomMemberData.COLUMN_UPDATED_AT, DbMeta.ChatRoomMemberData.ALIAS_UPDATED_AT,
            DbMeta.ChatRoomMemberData.COLUMN_IS_MUTED, DbMeta.ChatRoomMemberData.ALIAS_IS_MUTED,
            DbMeta.ChatRoomMemberData.COLUMN_IS_PINNED, DbMeta.ChatRoomMemberData.ALIAS_IS_PINNED,
            DbMeta.ChatRoomData.COLUMN_ID, DbMeta.ChatRoomData.ALIAS_ID,
            DbMeta.ChatRoomData.COLUMN_NAME, DbMeta.ChatRoomData.ALIAS_NAME,
            DbMeta.ChatRoomData.COLUMN_TYPE, DbMeta.ChatRoomData.ALIAS_TYPE,
            DbMeta.ChatRoomData.COLUMN_CREATED_BY, DbMeta.ChatRoomData.ALIAS_CREATED_BY,
            DbMeta.ChatRoomData.COLUMN_UPDATED_BY, DbMeta.ChatRoomData.ALIAS_UPDATED_BY,
            DbMeta.ChatRoomData.COLUMN_CREATED_AT, DbMeta.ChatRoomData.ALIAS_CREATED_AT,
            DbMeta.ChatRoomData.COLUMN_UPDATED_AT, DbMeta.ChatRoomData.ALIAS_UPDATED_AT,
            DbMeta.ChatRoomMemberData.TABLE_NAME,
            DbMeta.ChatRoomData.TABLE_NAME, DbMeta.ChatRoomMemberData.COLUMN_ROOM_ID, DbMeta.ChatRoomData.COLUMN_ID);
    private static final String FIND_BY_ID_SQL = BASIC_SQL + "where %s = $1".formatted(DbMeta.ChatRoomMemberData.COLUMN_ID);
    private static final String FIND_BY_ACCOUNT_ID_SQL = BASIC_SQL + "where %s = $1".formatted(DbMeta.ChatRoomMemberData.COLUMN_ACCOUNT_ID);
    private static final String FIND_BY_ROOM_ID_SQL = BASIC_SQL + "where %s = $1".formatted(DbMeta.ChatRoomMemberData.COLUMN_ROOM_ID);
    private static final String FIND_BY_ACCOUNT_ID_AND_ROOM_ID_SQL = BASIC_SQL + "where %s = $1 and %s = $1".formatted(DbMeta.ChatRoomMemberData.COLUMN_ACCOUNT_ID, DbMeta.ChatRoomMemberData.COLUMN_ROOM_ID);

    public ChatRoomMemberRepositoryImpl(ChatRoomMemberDataRepositoryPeer peer,
                                        R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    @Override
    public Mono<ChatRoomMember> findById(String id) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ID_SQL)
                .bind(0, id)
                .fetch()
                .one()
                .map(this::mapToData)
                .map(ChatRoomMemberDataMapper::dataToDomain);
    }

    @Override
    public Mono<ChatRoomMember> findByAccountAndRoomId(String accountId, String roomId) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ACCOUNT_ID_AND_ROOM_ID_SQL)
                .bind(0, accountId)
                .bind(1, roomId)
                .fetch()
                .one()
                .map(this::mapToData)
                .map(ChatRoomMemberDataMapper::dataToDomain);
    }

    @Override
    public Flux<ChatRoomMember> findAllByAccountId(String accountId) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ACCOUNT_ID_SQL)
                .bind(0, accountId)
                .fetch()
                .all()
                .map(this::mapToData)
                .map(ChatRoomMemberDataMapper::dataToDomain);
    }

    @Override
    public Flux<ChatRoomMember> findAllByRoomId(String roomId) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ROOM_ID_SQL)
                .bind(0, roomId)
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
    public Flux<ChatRoomMember> saveAll(List<ChatRoomMember> chatRoomMemberList) {
        return peer.saveAll(Flux.fromIterable(chatRoomMemberList).map(ChatRoomMemberDataMapper::domainToData))
                .map(ChatRoomMemberDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(id);
    }

    @Override
    public Mono<Void> deleteByRoomId(String roomId) {
        return peer.deleteByRoomId(roomId);
    }

    @Override
    public Mono<Void> deleteByAccountId(String accountId) {
        return peer.deleteByAccountId(accountId);
    }

    @Override
    public Mono<Boolean> existsByAccountIdAndRoomId(String accountId, String roomId) {
        return peer.existsByAccountIdAndRoomId(accountId, roomId);
    }

    @Override
    public Mono<Boolean> existsByIdAndRoomId(String id, String roomId) {
        return peer.existsByIdAndRoomId(id, roomId);
    }

    private ChatRoomMemberData mapToData(Map<String, Object> map) {
        return ChatRoomMemberData
                .builder()
                .id(map.get(DbMeta.ChatRoomMemberData.ALIAS_ID).toString())
                .accountId(map.get(DbMeta.ChatRoomMemberData.ALIAS_ACCOUNT_ID).toString())
                .roomId(map.get(DbMeta.ChatRoomMemberData.ALIAS_ROOM_ID).toString())
                .createdBy(map.get(DbMeta.ChatRoomMemberData.ALIAS_CREATED_BY).toString())
                .updatedBy(map.get(DbMeta.ChatRoomMemberData.ALIAS_UPDATED_BY).toString())
                .createdAt((Instant) map.get(DbMeta.ChatRoomMemberData.ALIAS_CREATED_AT))
                .updatedAt((Instant) map.get(DbMeta.ChatRoomMemberData.ALIAS_UPDATED_AT))
                .isMuted((Boolean) map.get(DbMeta.ChatRoomMemberData.ALIAS_IS_MUTED))
                .isPinned((Boolean) map.get(DbMeta.ChatRoomMemberData.ALIAS_IS_PINNED))
                .roomData(ChatRoomData.builder()
                        .id(map.get(DbMeta.ChatRoomData.ALIAS_ID).toString())
                        .name(map.get(DbMeta.ChatRoomData.ALIAS_NAME).toString())
                        .createdBy(map.get(DbMeta.ChatRoomData.ALIAS_CREATED_BY).toString())
                        .updatedBy(map.get(DbMeta.ChatRoomData.ALIAS_UPDATED_BY).toString())
                        .createdAt((Instant) map.get(DbMeta.ChatRoomData.ALIAS_CREATED_AT))
                        .updatedAt((Instant) map.get(DbMeta.ChatRoomData.ALIAS_UPDATED_AT))
                        .build())
                .build();
    }
}
