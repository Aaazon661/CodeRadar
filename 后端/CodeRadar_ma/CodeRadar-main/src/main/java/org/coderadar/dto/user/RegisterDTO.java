package org.coderadar.dto.user;

import lombok.Data;

@Data
public class RegisterDTO {
    private String account;
    private String username;
    private String passwordHash;
}