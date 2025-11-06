package com.nicolas.pulse.adapter.repository.user;

import com.nicolas.pulse.entity.domain.User;

import java.util.List;

public class UserDataMapper {
    public static User dataToDomain(UserData data) {
        if (data == null) {
            return null;
        }
        return User.builder()
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

    public static List<User> dataToDomain(List<UserData> data) {
        return data.stream().map(UserDataMapper::dataToDomain).toList();
    }

    public static UserData domainToData(User domain) {
        if (domain == null) {
            return null;
        }
        return UserData.builder()
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

    public static List<UserData> domainToData(List<User> domain) {
        return domain.stream().map(UserDataMapper::domainToData).toList();
    }
}
