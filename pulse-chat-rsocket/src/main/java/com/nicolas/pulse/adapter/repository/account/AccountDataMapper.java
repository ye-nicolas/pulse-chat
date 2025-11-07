package com.nicolas.pulse.adapter.repository.account;

import com.nicolas.pulse.entity.domain.Account;

import java.util.List;

public class AccountDataMapper {
    public static Account dataToDomain(AccountData data) {
        if (data == null) {
            return null;
        }
        return Account.builder()
                .id(data.getId())
                .name(data.getName())
                .showName(data.getShowName())
                .password(data.getPassword())
                .isActive(data.isActive())
                .lastLoginAt(data.getLastLoginAt())
                .createdBy(data.getCreatedBy())
                .updatedBy(data.getUpdatedBy())
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .remark(data.getRemark())
                .build();
    }

    public static List<Account> dataToDomain(List<AccountData> data) {
        return data.stream().map(AccountDataMapper::dataToDomain).toList();
    }

    public static AccountData domainToData(Account domain) {
        if (domain == null) {
            return null;
        }
        return AccountData.builder()
                .id(domain.getId())
                .name(domain.getName())
                .showName(domain.getShowName())
                .password(domain.getPassword())
                .isActive(domain.isActive())
                .lastLoginAt(domain.getLastLoginAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .remark(domain.getRemark())
                .build();
    }

    public static List<AccountData> domainToData(List<Account> domain) {
        return domain.stream().map(AccountDataMapper::domainToData).toList();
    }
}
