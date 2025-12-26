package com.nicolas.pulse.adapter.repository.friendship;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.account.AccountData;
import com.nicolas.pulse.entity.domain.FriendShip;
import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
import com.nicolas.pulse.service.repository.FriendShipRepository;
import com.nicolas.pulse.util.TypeUtil;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Map;

@Repository
public class FriendShipRepositoryImpl implements FriendShipRepository {
    private final FriendShipDataRepositoryPeer peer;
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
                    ac.%s AS %s,
                    ac.%s AS %s,
                    ac.%s AS %s,
                    ac.%s AS %s,
                    ac.%s AS %s,
                    ac.%s AS %s,
                    ac.%s AS %s,
                    ac.%s AS %s,
                    ac.%s AS %s,
                    ac.%s AS %s,
                    ac.%s AS %s,
                    ar.%s AS %s,
                    ar.%s AS %s,
                    ar.%s AS %s,
                    ar.%s AS %s,
                    ar.%s AS %s,
                    ar.%s AS %s,
                    ar.%s AS %s,
                    ar.%s AS %s,
                    ar.%s AS %s,
                    ar.%s AS %s,
                    ar.%s AS %s
                FROM %s
                LEFT JOIN %s ac ON %s = ac.%s
                LEFT JOIN %s ar ON %s = ar.%s
            """.formatted(
            DbMeta.FriendShipData.COLUMN_ID, DbMeta.FriendShipData.ALIAS_ID,
            DbMeta.FriendShipData.COLUMN_REQUESTER_ACCOUNT_ID, DbMeta.FriendShipData.ALIAS_REQUESTER_ACCOUNT_ID,
            DbMeta.FriendShipData.COLUMN_RECIPIENT_ACCOUNT_ID, DbMeta.FriendShipData.ALIAS_RECIPIENT_ACCOUNT_ID,
            DbMeta.FriendShipData.COLUMN_STATUS, DbMeta.FriendShipData.ALIAS_STATUS,
            DbMeta.FriendShipData.COLUMN_CREATED_BY, DbMeta.FriendShipData.ALIAS_CREATED_BY,
            DbMeta.FriendShipData.COLUMN_UPDATED_BY, DbMeta.FriendShipData.ALIAS_UPDATED_BY,
            DbMeta.FriendShipData.COLUMN_CREATED_AT, DbMeta.FriendShipData.ALIAS_CREATED_AT,
            DbMeta.FriendShipData.COLUMN_UPDATED_AT, DbMeta.FriendShipData.ALIAS_UPDATED_AT,

            DbMeta.AccountData.COL_ID, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_ID,
            DbMeta.AccountData.COL_NAME, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_NAME,
            DbMeta.AccountData.COL_SHOW_NAME, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_SHOW_NAME,
            DbMeta.AccountData.COL_PASSWORD, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_PASSWORD,
            DbMeta.AccountData.COL_IS_ACTIVE, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_IS_ACTIVE,
            DbMeta.AccountData.COL_LAST_LOGIN_AT, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_LAST_LOGIN_AT,
            DbMeta.AccountData.COL_CREATED_BY, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_CREATED_BY,
            DbMeta.AccountData.COL_UPDATED_BY, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_UPDATED_BY,
            DbMeta.AccountData.COL_CREATED_AT, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_CREATED_AT,
            DbMeta.AccountData.COL_UPDATED_AT, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_UPDATED_AT,
            DbMeta.AccountData.COL_REMARK, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_REMARK,
            DbMeta.AccountData.COL_ID, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_ID,
            DbMeta.AccountData.COL_NAME, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_NAME,
            DbMeta.AccountData.COL_SHOW_NAME, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_SHOW_NAME,
            DbMeta.AccountData.COL_PASSWORD, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_PASSWORD,
            DbMeta.AccountData.COL_IS_ACTIVE, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_IS_ACTIVE,
            DbMeta.AccountData.COL_LAST_LOGIN_AT, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_LAST_LOGIN_AT,
            DbMeta.AccountData.COL_CREATED_BY, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_CREATED_BY,
            DbMeta.AccountData.COL_UPDATED_BY, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_UPDATED_BY,
            DbMeta.AccountData.COL_CREATED_AT, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_CREATED_AT,
            DbMeta.AccountData.COL_UPDATED_AT, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_UPDATED_AT,
            DbMeta.AccountData.COL_REMARK, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_REMARK,
            DbMeta.FriendShipData.TABLE_NAME,
            DbMeta.AccountData.TABLE_NAME, DbMeta.FriendShipData.COLUMN_REQUESTER_ACCOUNT_ID, DbMeta.AccountData.COL_ID,
            DbMeta.AccountData.TABLE_NAME, DbMeta.FriendShipData.COLUMN_RECIPIENT_ACCOUNT_ID, DbMeta.AccountData.COL_ID
    );
    private static final String FIND_ALL_BY_ACCOUNT_ID = """
            (
                %s
                Where %s = $1
            )
            UNION ALL
            (
                %s
                Where %s = $2
            )
            """
            .formatted(BASIC_SQL, DbMeta.FriendShipData.COLUMN_REQUESTER_ACCOUNT_ID,
                    BASIC_SQL, DbMeta.FriendShipData.COLUMN_RECIPIENT_ACCOUNT_ID);
    private static final String FIND_BY_ID = BASIC_SQL + "where %s = $1".formatted(DbMeta.FriendShipData.COLUMN_ID);

    public FriendShipRepositoryImpl(FriendShipDataRepositoryPeer peer,
                                    R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    @Override
    public Flux<FriendShip> findAllByAccountId(String accountId) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_ALL_BY_ACCOUNT_ID)
                .bind(0, accountId)
                .bind(1, accountId)
                .fetch().all()
                .map(this::mapToData)
                .map(FriendShipDataMapper::dataToDomain);
    }

    @Override
    public Mono<FriendShip> findById(String id) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ID)
                .bind(0, id)
                .fetch()
                .one()
                .map(this::mapToData)
                .map(FriendShipDataMapper::dataToDomain);
    }

    public FriendShipData mapToData(Map<String, Object> map) {
        return FriendShipData.builder()
                .id((String) map.get(DbMeta.FriendShipData.ALIAS_ID))
                .requesterAccountId((String) map.get(DbMeta.FriendShipData.ALIAS_REQUESTER_ACCOUNT_ID))
                .recipientAccountId((String) map.get(DbMeta.FriendShipData.ALIAS_RECIPIENT_ACCOUNT_ID))
                .status(StringUtils.hasText((String) map.get(DbMeta.FriendShipData.ALIAS_STATUS)) ? FriendShipStatus.valueOf((String) map.get(DbMeta.FriendShipData.ALIAS_STATUS)) : null)
                .createdBy((String) map.get(DbMeta.FriendShipData.ALIAS_CREATED_BY))
                .updatedBy((String) map.get(DbMeta.FriendShipData.ALIAS_UPDATED_BY))
                .createdAt((OffsetDateTime) map.get(DbMeta.FriendShipData.ALIAS_CREATED_AT))
                .updatedAt((OffsetDateTime) map.get(DbMeta.FriendShipData.ALIAS_UPDATED_AT))
                .requesterAccount(AccountData.builder()
                        .id((String) map.get(DbMeta.FriendShipData.ALIAS_REQUESTER_ACCOUNT_ID))
                        .name((String) map.get(DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_NAME))
                        .showName((String) map.get(DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_SHOW_NAME))
                        .password((String) map.get(DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_PASSWORD))
                        .isActive((Boolean) map.get(DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_IS_ACTIVE))
                        .createdBy((String) map.get(DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_CREATED_BY))
                        .updatedBy((String) map.get(DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_UPDATED_BY))
                        .createdAt(TypeUtil.toInstant((OffsetDateTime) map.get(DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_CREATED_AT)))
                        .updatedAt(TypeUtil.toInstant((OffsetDateTime) map.get(DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_UPDATED_AT)))
                        .remark((String) map.get(DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID + DbMeta.AccountData.COL_REMARK))
                        .build())
                .recipientAccount(AccountData.builder()
                        .id((String) map.get(DbMeta.FriendShipData.ALIAS_RECIPIENT_ACCOUNT_ID))
                        .name((String) map.get(DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_NAME))
                        .showName((String) map.get(DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_SHOW_NAME))
                        .password((String) map.get(DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_PASSWORD))
                        .isActive((Boolean) map.get(DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_IS_ACTIVE))
                        .createdBy((String) map.get(DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_CREATED_BY))
                        .updatedBy((String) map.get(DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_UPDATED_BY))
                        .createdAt(TypeUtil.toInstant((OffsetDateTime) map.get(DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_CREATED_AT)))
                        .updatedAt(TypeUtil.toInstant((OffsetDateTime) map.get(DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_UPDATED_AT)))
                        .remark((String) map.get(DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID + DbMeta.AccountData.COL_REMARK))
                        .build())
                .build();
    }

    @Override
    public Mono<FriendShip> save(FriendShip friendShip) {
        FriendShipData friendShipData = FriendShipDataMapper.domainToData(friendShip);
        return peer.save(friendShipData).map(FriendShipDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsByRequesterAccountIdAndRecipientAccountId(String requesterAccountId, String recipientAccountId) {
        return Mono.zip(peer.existsByRequesterAccountIdAndRecipientAccountId(requesterAccountId, recipientAccountId),
                        peer.existsByRequesterAccountIdAndRecipientAccountId(recipientAccountId, requesterAccountId))
                .map(tuple -> tuple.getT1() || tuple.getT2());
    }
}
