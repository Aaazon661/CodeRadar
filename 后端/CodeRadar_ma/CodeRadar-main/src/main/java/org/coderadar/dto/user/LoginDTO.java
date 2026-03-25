package org.coderadar.dto.user;

import lombok.Data;

@Data
public class LoginDTO {
    private String account;
    private String passwordHash;
}