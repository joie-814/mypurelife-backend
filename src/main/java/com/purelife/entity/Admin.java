package com.purelife.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("admins")
public class Admin {

    @Id
    @Column("admin_id")
    private Integer adminId;

    @Column("account")
    private String account;

    @Column("password_hash")
    private String passwordHash;

    @Column("name")
    private String name;

    @Column("role")
    private String role;

    @Column("is_active")
    private Boolean isActive;

    @Column("created_at")
    private LocalDateTime createdAt;
}