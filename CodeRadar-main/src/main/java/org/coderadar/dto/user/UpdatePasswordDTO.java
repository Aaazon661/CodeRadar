package org.coderadar.dto.user;

import lombok.Data;

@Data
public class UpdatePasswordDTO {
    private String passwordHash;
}