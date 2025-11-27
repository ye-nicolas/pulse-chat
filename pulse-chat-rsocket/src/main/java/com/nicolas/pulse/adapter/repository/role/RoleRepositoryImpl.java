package com.nicolas.pulse.adapter.repository.role;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.entity.domain.Role;
import com.nicolas.pulse.entity.enumerate.Privilege;
import com.nicolas.pulse.service.repository.RoleRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class RoleRepositoryImpl implements RoleRepository {
    private final RoleDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;
    private static final String BASIC_SQL = """
             SELECT
                   r.%s AS %s,
                   r.%s AS %s,
                   r.%s AS %s,
                   r.%s AS %s,
                   r.%s AS %s,
                   r.%s AS %s,
                   r.%s AS %s,
                   rp.%s AS %s
            FROM %s r
            LEFT JOIN %s rp ON r.%s = rp.%s
            """.formatted(
            DbMeta.RoleData.COLUMN_ID, DbMeta.RoleData.ALIAS_ID,
            DbMeta.RoleData.COLUMN_NAME, DbMeta.RoleData.ALIAS_NAME,
            DbMeta.RoleData.COLUMN_CREATED_BY, DbMeta.RoleData.ALIAS_CREATED_BY,
            DbMeta.RoleData.COLUMN_UPDATED_BY, DbMeta.RoleData.ALIAS_UPDATED_BY,
            DbMeta.RoleData.COLUMN_CREATED_AT, DbMeta.RoleData.ALIAS_CREATED_AT,
            DbMeta.RoleData.COLUMN_UPDATED_AT, DbMeta.RoleData.ALIAS_UPDATED_AT,
            DbMeta.RoleData.COLUMN_REMARK, DbMeta.RoleData.ALIAS_REMARK,
            DbMeta.RolePrivilegeData.COLUMN_PRIVILEGE, DbMeta.RolePrivilegeData.ALIAS_PRIVILEGE,
            DbMeta.RoleData.TABLE_NAME,
            DbMeta.RolePrivilegeData.TABLE_NAME,
            DbMeta.RoleData.COLUMN_ID,
            DbMeta.RolePrivilegeData.COLUMN_ROLE_ID);

    private static final String FIND_BY_ID = BASIC_SQL + "WHERE r.%s = $1".formatted(DbMeta.RoleData.COLUMN_ID);
    private static final String FIND_BY_MORE_ID = BASIC_SQL + "WHERE r.%s = ANY($1)".formatted(DbMeta.RoleData.COLUMN_ID);

    public RoleRepositoryImpl(RoleDataRepositoryPeer peer,
                              R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    @Override
    public Flux<Role> findAll() {
        return r2dbcEntityOperations.getDatabaseClient().sql(BASIC_SQL)
                .fetch()
                .all()
                .bufferUntilChanged(a -> a.get(DbMeta.RoleData.ALIAS_ID).toString())
                .map(RoleRepositoryImpl::mapToData)
                .map(RoleDataMapper::dataToDomain);
    }

    @Override
    public Flux<Role> findAllByIds(String[] ids) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_MORE_ID)
                .bind(0, ids)
                .fetch()
                .all()
                .bufferUntilChanged(a -> a.get(DbMeta.RoleData.ALIAS_ID).toString())
                .map(RoleRepositoryImpl::mapToData)
                .map(RoleDataMapper::dataToDomain);
    }

    public static RoleData mapToData(List<Map<String, Object>> listMap) {
        return RoleData.builder()
                .id((String) listMap.getFirst().get(DbMeta.RoleData.ALIAS_ID))
                .name((String) listMap.getFirst().get(DbMeta.RoleData.ALIAS_NAME))
                .createdBy((String) listMap.getFirst().get(DbMeta.RoleData.ALIAS_CREATED_BY))
                .updatedBy((String) listMap.getFirst().get(DbMeta.RoleData.ALIAS_UPDATED_BY))
                .createdAt((OffsetDateTime) listMap.getFirst().get(DbMeta.RoleData.ALIAS_CREATED_AT))
                .updatedAt((OffsetDateTime) listMap.getFirst().get(DbMeta.RoleData.ALIAS_UPDATED_AT))
                .remark((String) listMap.getFirst().get(DbMeta.RoleData.ALIAS_REMARK))
                .privilegeSet(listMap.stream()
                        .map(m -> m.get(DbMeta.RolePrivilegeData.ALIAS_PRIVILEGE))
                        .filter(Objects::nonNull)
                        .map(Object::toString)                   // 保險轉成 String
                        .map(Privilege::valueOf)                 // 轉成 Enum
                        .collect(Collectors.toSet())
                ).build();
    }

    @Override
    public Mono<Role> findById(String id) {
        return r2dbcEntityOperations.getDatabaseClient().sql(FIND_BY_ID)
                .bind(0, id)
                .fetch()
                .all()
                .bufferUntilChanged(a -> a.get(DbMeta.RoleData.ALIAS_ID).toString())
                .singleOrEmpty()
                .map(RoleRepositoryImpl::mapToData)
                .map(RoleDataMapper::dataToDomain);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return peer.existsById(id);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return peer.existsByName(name);
    }

    @Override
    public Mono<Role> save(Role role) {
        RoleData roleData = RoleDataMapper.domainToData(role);
        return r2dbcEntityOperations.insert(roleData).map(RoleDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(id);
    }
}
