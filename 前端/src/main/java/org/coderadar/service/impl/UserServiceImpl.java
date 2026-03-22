package org.coderadar.service.impl;

import lombok.RequiredArgsConstructor;
import org.coderadar.dao.UserDAO;
import org.coderadar.pojo.User;
import org.coderadar.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Override
    public User register(String account, String username, String passwordHash) {

        User exist = userDAO.findByAccount(account);
        if (exist != null) {
            return null;
        }

        User user = User.builder()
                .account(account)
                .username(username)
                .passwordHash(passwordHash)
                .build();

        userDAO.insert(user);
        return user;
    }

    @Override
    public User login(String account, String passwordHash) {

        User user = userDAO.findByAccount(account);
        if (user == null) return null;

        if (!user.getPasswordHash().equals(passwordHash)) return null;

        userDAO.updateLastLoginTime(user.getUserId());
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        return userDAO.findById(userId);
    }

    @Override
    public int updateUsername(Long userId, String username) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        return userDAO.updateUsername(user);
    }

    @Override
    public int updatePassword(Long userId, String passwordHash) {
        User user = new User();
        user.setUserId(userId);
        user.setPasswordHash(passwordHash);
        return userDAO.updatePassword(user);
    }

    @Override
    public int disableUser(Long userId) {
        return userDAO.disableUser(userId);
    }
}
