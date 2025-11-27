package com.nicolas.pulse.adapter.repository.account;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.role.RoleData;
import com.nicolas.pulse.adapter.repository.role.RoleRepositoryImpl;
import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.entity.enumerate.Privilege;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.RoleRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AccountRepositoryImpl implements AccountRepository {
    private final AccountDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;
    private static final String ROLE_PREFIX = "role_";
    private static final String ROLE_PRIVILEGE_PREFIX = "rp_";
    private static final String BASIC_SQL = """
            SELECT
                a.%s as %s,
                a.%s as %s,
                a.%s as %s,
                a.%s as %s,
                a.%s as %s,
                a.%s as %s,
                a.%s as %s,
                a.%s as %s,
                a.%s as %s,
                a.%s as %s,
                a.%s as %s,
                r.%s as %s,
                r.%s as %s,
                r.%s as %s,
                r.%s as %s,
                r.%s as %s,
                r.%s as %s,
                r.%s as %s,
                rp.%s as %s
            FROM %s a
            LEFT JOIN %s ar on a.%s = ar.%s
            LEFT JOIN %s r on r.%s = ar.%s
            LEFT JOIN %s rp on r.%s = rp.%s
            """.formatted(
            DbMeta.AccountData.COLUMN_ID, DbMeta.AccountData.ALIAS_ID,
            DbMeta.AccountData.COLUMN_NAME, DbMeta.AccountData.ALIAS_NAME,
            DbMeta.AccountData.COLUMN_SHOW_NAME, DbMeta.AccountData.ALIAS_SHOW_NAME,
            DbMeta.AccountData.COLUMN_PASSWORD, DbMeta.AccountData.ALIAS_PASSWORD,
            DbMeta.AccountData.COLUMN_IS_ACTIVE, DbMeta.AccountData.ALIAS_IS_ACTIVE,
            DbMeta.AccountData.COLUMN_LAST_LOGIN_AT, DbMeta.AccountData.ALIAS_LAST_LOGIN_AT,
            DbMeta.AccountData.COLUMN_CREATED_BY, DbMeta.AccountData.ALIAS_CREATED_BY,
            DbMeta.AccountData.COLUMN_UPDATED_BY, DbMeta.AccountData.ALIAS_UPDATED_BY,
            DbMeta.AccountData.COLUMN_CREATED_AT, DbMeta.AccountData.ALIAS_CREATED_AT,
            DbMeta.AccountData.COLUMN_UPDATED_AT, DbMeta.AccountData.ALIAS_UPDATED_AT,
            DbMeta.AccountData.COLUMN_REMARK, DbMeta.AccountData.ALIAS_REMARK,
            DbMeta.RoleData.COLUMN_ID, DbMeta.RoleData.ALIAS_ID,
            DbMeta.RoleData.COLUMN_NAME, DbMeta.RoleData.ALIAS_NAME,
            DbMeta.RoleData.COLUMN_CREATED_BY, DbMeta.RoleData.ALIAS_CREATED_BY,
            DbMeta.RoleData.COLUMN_UPDATED_BY, DbMeta.RoleData.ALIAS_UPDATED_BY,
            DbMeta.RoleData.COLUMN_CREATED_AT, DbMeta.RoleData.ALIAS_CREATED_AT,
            DbMeta.RoleData.COLUMN_UPDATED_AT, DbMeta.RoleData.ALIAS_UPDATED_AT,
            DbMeta.RoleData.COLUMN_REMARK, DbMeta.RoleData.ALIAS_REMARK,
            DbMeta.RolePrivilegeData.COLUMN_PRIVILEGE, DbMeta.RolePrivilegeData.ALIAS_PRIVILEGE,
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
                .bufferUntilChanged(m -> m.get(DbMeta.AccountData.ALIAS_ID).toString())
                .flatMap(accountChunk -> Flux.fromIterable(accountChunk)
                        .bufferUntilChanged(map -> map.get(DbMeta.RoleData.ALIAS_ID))
                        // Mono & Flux中不能有null，需要先過濾
                        .filter(mapList -> mapList.getFirst().get(DbMeta.RoleData.ALIAS_ID) != null)
                        .map(RoleRepositoryImpl::mapToData)
                        .collectList()
                        .map(roleDataList -> toMapToData(accountChunk, roleDataList)))
                .map(AccountDataMapper::dataToDomain);
    }

    private AccountData toMapToData(List<Map<String, Object>> mapList, List<RoleData> roleDataList) {
        return AccountData.builder()
                .id((String) mapList.getFirst().get(DbMeta.AccountData.ALIAS_ID))
                .name((String) mapList.getFirst().get(DbMeta.AccountData.ALIAS_NAME))
                .password((String) mapList.getFirst().get(DbMeta.AccountData.ALIAS_PASSWORD))
                .showName((String) mapList.getFirst().get(DbMeta.AccountData.ALIAS_SHOW_NAME))
                .lastLoginAt((OffsetDateTime) mapList.getFirst().get(DbMeta.AccountData.ALIAS_LAST_LOGIN_AT))
                .isActive(Optional.ofNullable(mapList.getFirst().get(DbMeta.AccountData.ALIAS_IS_ACTIVE))
                        .map(Boolean.class::cast)
                        .orElse(false))
                .createdBy((String) mapList.getFirst().get(DbMeta.AccountData.ALIAS_CREATED_BY))
                .updatedBy((String) mapList.getFirst().get(DbMeta.AccountData.ALIAS_UPDATED_BY))
                .createdAt((OffsetDateTime) mapList.getFirst().get(DbMeta.AccountData.ALIAS_CREATED_AT))
                .updatedAt((OffsetDateTime) mapList.getFirst().get(DbMeta.AccountData.ALIAS_UPDATED_AT))
                .remark(Optional.ofNullable(mapList.getFirst().get(DbMeta.AccountData.ALIAS_REMARK)).map(String.class::cast).orElse(null))
                .roleDataList(roleDataList)
                .build();
    }

    @Override
    public Mono<Account> findById(String id) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ID)
                .bind(0, id)
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
                .bind(0, name)
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
