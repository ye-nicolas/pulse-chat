package com.nicolas.util;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.account.AccountData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class AccountDataMapping {
    public static final Map<String, Function<AccountData, ?>> MAPPING = new LinkedHashMap<>();

    static {
        MAPPING.put(DbMeta.AccountData.COL_ID, AccountData::getId);
        MAPPING.put(DbMeta.AccountData.COL_NAME, AccountData::getName);
        MAPPING.put(DbMeta.AccountData.COL_SHOW_NAME, AccountData::getShowName);
        MAPPING.put(DbMeta.AccountData.COL_PASSWORD, AccountData::getPassword);
        MAPPING.put(DbMeta.AccountData.COL_IS_ACTIVE, AccountData::isActive);
        MAPPING.put(DbMeta.AccountData.COL_REMARK, AccountData::getRemark);
        MAPPING.put(DbMeta.AccountData.COL_CREATED_BY, AccountData::getCreatedBy);
        MAPPING.put(DbMeta.AccountData.COL_UPDATED_BY, AccountData::getUpdatedBy);
        MAPPING.put(DbMeta.AccountData.COL_CREATED_AT, AccountData::getCreatedAt);
        MAPPING.put(DbMeta.AccountData.COL_UPDATED_AT, AccountData::getUpdatedAt);
    }
}
