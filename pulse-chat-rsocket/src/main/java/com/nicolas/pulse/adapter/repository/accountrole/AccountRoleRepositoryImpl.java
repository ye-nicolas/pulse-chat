package com.nicolas.pulse.adapter.repository.accountrole;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.role.RoleData;
import com.nicolas.pulse.adapter.repository.role.RoleRepositoryImpl;
import com.nicolas.pulse.entity.domain.AccountRole;
import com.nicolas.pulse.service.repository.AccountRoleRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public class AccountRoleRepositoryImpl implements AccountRoleRepository {
    private final AccountRoleDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;

    public AccountRoleRepositoryImpl(AccountRoleDataRepositoryPeer peer,
                                     R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    private static final String BASIC_SQL = """
            SELECT
                %s as %s,
                %s as %s,
                %s as %s,
                %s as %s,
                %s as %s,
                %s as %s,
                %s as %s,
                %s as %s,
                %s as %s,
                %s as %s,
                %s as %s,
                %s as %s,
                %s as %s
            FROM %s
            LEFT JOIN %s on %s = %s
            LEFT JOIN %s on %s = %s
            """.formatted(
            DbMeta.AccountRoleData.COLUMN_ID, DbMeta.AccountRoleData.ALIAS_ID,
            DbMeta.AccountRoleData.COLUMN_ACCOUNT_ID, DbMeta.AccountRoleData.ALIAS_ACCOUNT_ID,
            DbMeta.AccountRoleData.COLUMN_ROLE_ID, DbMeta.AccountRoleData.ALIAS_ROLE_ID,
            DbMeta.AccountRoleData.COLUMN_CREATED_BY, DbMeta.AccountRoleData.ALIAS_CREATED_BY,
            DbMeta.AccountRoleData.COLUMN_CREATED_AT, DbMeta.AccountRoleData.ALIAS_CREATED_AT,
            DbMeta.RoleData.COLUMN_ID, DbMeta.RoleData.ALIAS_ID,
            DbMeta.RoleData.COLUMN_NAME, DbMeta.RoleData.ALIAS_NAME,
            DbMeta.RoleData.COLUMN_CREATED_BY, DbMeta.RoleData.ALIAS_CREATED_BY,
            DbMeta.RoleData.COLUMN_UPDATED_BY, DbMeta.RoleData.ALIAS_UPDATED_BY,
            DbMeta.RoleData.COLUMN_CREATED_AT, DbMeta.RoleData.ALIAS_CREATED_AT,
            DbMeta.RoleData.COLUMN_UPDATED_AT, DbMeta.RoleData.ALIAS_UPDATED_AT,
            DbMeta.RoleData.COLUMN_REMARK, DbMeta.RoleData.ALIAS_REMARK,
            DbMeta.RolePrivilegeData.COLUMN_PRIVILEGE, DbMeta.RolePrivilegeData.ALIAS_PRIVILEGE,
            DbMeta.AccountRoleData.TABLE_NAME,
            DbMeta.RoleData.TABLE_NAME, DbMeta.AccountRoleData.COLUMN_ROLE_ID, DbMeta.RoleData.COLUMN_ID,
            DbMeta.RolePrivilegeData.TABLE_NAME, DbMeta.RoleData.COLUMN_ID, DbMeta.RolePrivilegeData.COLUMN_ROLE_ID
    );
    private static final String FIND_BY_ACCOUNT_ID = BASIC_SQL + "WHERE ar.%s = $1".formatted(DbMeta.AccountRoleData.COLUMN_ACCOUNT_ID);
    private static final String FIND_BY_ROLE_ID = BASIC_SQL + "WHERE ar.%s = $1".formatted(DbMeta.AccountRoleData.COLUMN_ROLE_ID);


    @Override
    public Flux<AccountRole> findAllByAccountId(String accountId) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ACCOUNT_ID)
                .bind(0, accountId)
                .fetch()
                .all()
                .bufferUntilChanged(m -> m.get(DbMeta.AccountRoleData.ALIAS_ROLE_ID).toString())
                .map(accountChunk -> this.toMapToData(accountChunk, RoleRepositoryImpl.mapToData(accountChunk)))
                .map(AccountRoleDataMapper::dataToDomain);
    }


    @Override
    public Flux<AccountRole> findAllByRoleId(String roleId) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ROLE_ID)
                .bind(0, roleId)
                .fetch()
                .all()
                .bufferUntilChanged(m -> m.get(DbMeta.AccountRoleData.ALIAS_ROLE_ID).toString())
                .map(accountChunk -> this.toMapToData(accountChunk, RoleRepositoryImpl.mapToData(accountChunk)))
                .map(AccountRoleDataMapper::dataToDomain);
    }

    private AccountRoleData toMapToData(List<Map<String, Object>> mapList, RoleData roleData) {
        return AccountRoleData.builder()
                .id((String) mapList.getFirst().get(DbMeta.AccountRoleData.ALIAS_ID))
                .accountId((String) mapList.getFirst().get(DbMeta.AccountRoleData.ALIAS_ACCOUNT_ID))
                .roleId((String) mapList.getFirst().get(DbMeta.AccountRoleData.ALIAS_ROLE_ID))
                .createdBy((String) mapList.getFirst().get(DbMeta.AccountRoleData.ALIAS_CREATED_BY))
                .createdAt((Instant) mapList.getFirst().get(DbMeta.AccountRoleData.ALIAS_CREATED_AT))
                .roleData(roleData)
                .build();
    }

    @Override
    public Flux<AccountRole> saveAll(List<AccountRole> accountRoleList) {
        return peer.saveAll(accountRoleList.stream().map(AccountRoleDataMapper::domainToData).toList())
                .map(AccountRoleDataMapper::dataToDomain);
    }

    @Override
    public Mono<Boolean> existsByAccountIdAndRoleId(String accountId, String roleId) {
        return peer.existsByAccountIdAndRoleId(accountId, roleId);
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
    public Mono<Void> deleteByRoleId(String roleId) {
        return peer.deleteByRoleId(roleId);
    }
}
