package com.purelife.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("members")
@Schema(description = "會員")// Swagger 文件中的描述
public class Member {
    
    @Id
    @Column("member_id") // 對應資料庫的欄位名稱（因為資料庫標準寫法為snake_case，而Java為camelCase，故使用@Column當作橋樑）
    @Schema(description = "會員ID", example = "1")
    private Integer memberId;
    
    @Column("account")
    @Schema(description = "帳號", example = "joie")
    private String account;
    
    @JsonIgnore  // 回傳 JSON 時不包含密碼
    @Schema(hidden = true)  // Swagger 文件隱藏
    @Column("password_hash")
    private String passwordHash;
    
    @Column("name")
    @Schema(description = "姓名", example = "Joie")
    private String name;
    
    @Column("email")
    @Schema(description = "Email", example = "joie@example.com")
    private String email;
    
    @Column("phone")
    @Schema(description = "手機", example = "0912345678")
    private String phone;
    
    @Column("member_level")
    @Schema(description = "會員等級", example = "general")
    private String memberLevel;
    
    @Column("registration_time")
    @Schema(description = "註冊時間")
    private LocalDateTime registrationTime;
    
    @Column("created_at")
    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
    
    @Column("is_active")
    @Schema(description = "是否啟用", example = "true")
    private Boolean isActive;
}