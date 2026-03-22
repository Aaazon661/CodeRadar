package org.coderadar.service;

import org.coderadar.pojo.User;

public interface UserService {
    User register(String account, String username, String passwordHash);

    User login(String account, String passwordHash);

    User getUserById(Long userId);

    int updateUsername(Long userId, String username);

    int updatePassword(Long userId, String passwordHash);

    int disableUser(Long userId);
}

