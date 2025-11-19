package com.nicolas.pulse.adapter.repository.account;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.role.RoleData;
import com.nicolas.pulse.adapter.repository.role.RoleRepositoryImpl;
import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.service.repository.AccountRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {
    private final AccountDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;
    private static final String ROLE_PREFIX = "role_";
    private static final String ROLE_PRIVILEGE_PREFIX = "rp_";
    private static final String BASIC_SQL = """
            SELECT
                a.*,
                r.%s as %s,
                r.%s as %s,
                r.%s as %s,
                r.%s as %s,
                r.%s as %s,
                r.%s as %s,
                r.%s as %s,
                rp.%s as %s
            FROM %s a
            left join %s ar on a.%s = ar.%s
            join %s r on r.%s = ar.%s
            join %s rp on r.%s = rp.%s
            """.formatted(
            DbMeta.RoleData.COLUMN_ID, ROLE_PREFIX + DbMeta.RoleData.COLUMN_ID,
            DbMeta.RoleData.COLUMN_NAME, ROLE_PREFIX + DbMeta.RoleData.COLUMN_NAME,
            DbMeta.RoleData.COLUMN_CREATED_BY, ROLE_PREFIX + DbMeta.RoleData.COLUMN_CREATED_BY,
            DbMeta.RoleData.COLUMN_UPDATED_BY, ROLE_PREFIX + DbMeta.RoleData.COLUMN_UPDATED_BY,
            DbMeta.RoleData.COLUMN_CREATED_AT, ROLE_PREFIX + DbMeta.RoleData.COLUMN_CREATED_AT,
            DbMeta.RoleData.COLUMN_UPDATED_AT, ROLE_PREFIX + DbMeta.RoleData.COLUMN_UPDATED_AT,
            DbMeta.RoleData.COLUMN_REMARK, ROLE_PREFIX + DbMeta.RoleData.COLUMN_REMARK,
            DbMeta.RolePrivilegeData.COLUMN_PRIVILEGE, ROLE_PRIVILEGE_PREFIX + DbMeta.RolePrivilegeData.COLUMN_PRIVILEGE,
            DbMeta.AccountData.TABLE_NAME,
            DbMeta.AccountRoleData.TABLE_NAME,
            DbMeta.AccountData.COLUMN_ID,
            DbMeta.AccountRoleData.COLUMN_ACCOUNT_ID,
            DbMeta.RoleData.TABLE_NAME,
            DbMeta.RoleData.COLUMN_ID,
            DbMeta.AccountRoleData.COLUMN_ROLE_ID,
            DbMeta.RolePrivilegeData.TABLE_NAME,
            DbMeta.RoleData.COLUMN_ID,
            DbMeta.RolePrivilegeData.COLUMN_ROLE_ID
    );
    private static final String FIND_BY_ID = BASIC_SQL + "WHERE a.%s = $1".formatted(DbMeta.AccountData.COLUMN_ID);
    private static final String FIND_BY_NAME = BASIC_SQL + "WHERE a.%s = $1".formatted(DbMeta.AccountData.COLUMN_NAME);

    public AccountRepositoryImpl(AccountDataRepositoryPeer peer,
                                 R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    public static void main(String[] args) {
        System.out.println(BASIC_SQL);
    }

    @Override
    public Flux<Account> findAll() {
        return r2dbcEntityOperations.getDatabaseClient().sql(BASIC_SQL)
                .fetch()
                .all()
                .bufferUntilChanged(m -> m.get(DbMeta.AccountData.COLUMN_ID).toString())
                .flatMap(accountChunk -> Flux.fromIterable(accountChunk)
                        .bufferUntilChanged(m -> m.get(ROLE_PREFIX + DbMeta.RoleData.COLUMN_ID).toString())
                        .map(RoleRepositoryImpl::mapToData)
                        .collectList()
                        .map(roleDataList -> toMapToData(accountChunk, roleDataList)))
                .map(AccountDataMapper::dataToDomain);
    }

    private AccountData toMapToData(List<Map<String, Object>> mapList, List<RoleData> roleDataList) {
        return AccountData.builder()
                .id((String) mapList.getFirst().get(DbMeta.AccountData.COLUMN_ID))
                .name((String) mapList.getFirst().get(DbMeta.AccountData.COLUMN_NAME))
                .password((String) mapList.getFirst().get(DbMeta.AccountData.COLUMN_PASSWORD))
                .showName((String) mapList.getFirst().get(DbMeta.AccountData.COLUMN_SHOW_NAME))
                .lastLoginAt((OffsetDateTime) mapList.getFirst().get(DbMeta.AccountData.COLUMN_LAST_LOGIN_AT))
                .isActive(Optional.ofNullable(mapList.getFirst().get(DbMeta.AccountData.COLUMN_IS_ACTIVE))
                        .map(Boolean.class::cast)
                        .orElse(false))
                .createdBy((String) mapList.getFirst().get(DbMeta.AccountData.COLUMN_CREATED_BY))
                .updatedBy((String) mapList.getFirst().get(DbMeta.AccountData.COLUMN_UPDATED_BY))
                .createdAt((OffsetDateTime) mapList.getFirst().get(DbMeta.AccountData.COLUMN_CREATED_AT))
                .updatedAt((OffsetDateTime) mapList.getFirst().get(DbMeta.AccountData.COLUMN_UPDATED_AT))
                .remark(Optional.ofNullable(mapList.getFirst().get(DbMeta.AccountData.COLUMN_REMARK)).map(String.class::cast).orElse(null))
                .roleDataList(roleDataList)
                .build();
    }

    @Override
    public Mono<Account> findById(String id) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ID)
                .bind(1, id)
                .fetch()
                .all()
                .bufferUntilChanged(m -> m.get(DbMeta.AccountData.COLUMN_ID).toString())
                .flatMap(accountChunk -> Flux.fromIterable(accountChunk)
                        .bufferUntilChanged(m -> m.get(ROLE_PREFIX + DbMeta.RoleData.COLUMN_ID).toString())
                        .map(RoleRepositoryImpl::mapToData)
                        .collectList()
                        .map(roleDataList -> toMapToData(accountChunk, roleDataList)))
                .singleOrEmpty()
                .map(AccountDataMapper::dataToDomain);
    }

    @Override
    public Mono<Account> findByName(String name) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_NAME)
                .bind(1, name)
                .fetch()
                .all()
                .bufferUntilChanged(m -> m.get(DbMeta.AccountData.COLUMN_ID).toString())
                .flatMap(accountChunk -> Flux.fromIterable(accountChunk)
                        .bufferUntilChanged(m -> m.get(ROLE_PREFIX + DbMeta.RoleData.COLUMN_ID).toString())
                        .map(RoleRepositoryImpl::mapToData)
                        .collectList()
                        .map(roleDataList -> toMapToData(accountChunk, roleDataList)))
                .singleOrEmpty()
                .map(AccountDataMapper::dataToDomain);
    }

    @Override
    public Mono<Account> create(Account account) {
        OffsetDateTime now = OffsetDateTime.now();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        AccountData accountData = AccountDataMapper.domainToData(account);
        return r2dbcEntityOperations.insert(accountData).map(AccountDataMapper::dataToDomain);
    }

    @Override
    public Mono<Account> update(Account account) {
        account.setUpdatedAt(OffsetDateTime.now());
        AccountData accountData = AccountDataMapper.domainToData(account);
        return r2dbcEntityOperations.update(accountData).map(AccountDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return peer.existsByName(name);
    }
}
