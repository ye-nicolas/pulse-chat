package com.nicolas.pulse.adapter.repository.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;


@Table(value = "account")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountData {
    @Id
    @Column("id")
    private String id;
    @Column("name")
    private String name;
    @Column("show_name")
    private String showName;
    @Column("password")
    private String password;
    @Column("is_active")
    private boolean isActive;
    @Column("last_login_at")
    private LocalDateTime lastLoginAt;
    @Column("created_by")
    private String createdBy;
    @Column("updated_by")
    private String updatedBy;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("remark")
    private String remark;
}
