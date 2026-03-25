package org.coderadar.pojo;

import lombok.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long userId;
    /** 登录账号：建议唯一（邮箱/手机号/学号等） */
    private String account;
    /** 展示用户名：可不唯一 */
    private String username;
    /** 密码哈希（不要存明文） */
    private String passwordHash;
    /** 可选：盐（如果你们不用bcrypt，可用盐） */
    private String salt;
    /** ENABLED / DISABLED */
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}
