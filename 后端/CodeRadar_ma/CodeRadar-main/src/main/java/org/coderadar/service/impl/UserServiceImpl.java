package org.coderadar.service.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coderadar.dao.UserDAO;
import org.coderadar.pojo.User;
import org.coderadar.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Override
    @Transactional
    public User register(String account, String username, String passwordHash) {
        // 检查账号是否已存在
        User exist = userDAO.findByAccount(account);
        if (exist != null) {
            log.warn("注册失败，账号已存在: {}", account);
            return null;
        }

        // 创建新用户，补充属性
        User user = User.builder()
                .account(account)
                .username(username)
                .passwordHash(passwordHash)
                .status("ACTIVE") // 默认激活状态
                .build();

        // 使用带注解的方法执行插入，避免 MyBatis 找不到映射
        userDAO.insertUser(user);
        log.info("用户注册成功: userId={}, account={}", user.getUserId(), account);
        return user;
    }

    @Override
    @Transactional
    public User login(String account, String passwordHash) {
        User user = userDAO.findByAccount(account);
        if (user == null) {
            log.warn("登录失败，用户不存在: {}", account);
            return null;
        }

        normalizeUserStatus(user);

        if (!Objects.equals(user.getPasswordHash(), passwordHash)) {
            log.warn("登录失败，密码错误: {}", account);
            return null;
        }

        if ("DISABLED".equals(user.getStatus())) {
            log.warn("登录失败，用户已禁用: {}", account);
            return null;
        }

        try {
            userDAO.updateLastLoginTime(user.getUserId());
        } catch (Exception e) {
            // 避免因 last_login_at 列缺失等导致整次登录 500，仍允许登录成功
            log.warn("更新最后登录时间失败（可检查表结构是否含 last_login_at）: {}", e.getMessage());
        }
        log.info("用户登录成功: userId={}, account={}", user.getUserId(), account);
        return user;
    }

    /**
     * 兼容库中 status 为 TINYINT(0/1) 或 VARCHAR（ACTIVE/DISABLED）两种写法。
     */
    private void normalizeUserStatus(User user) {
        if (user == null) {
            return;
        }
        String s = user.getStatus();
        if (s == null || s.isBlank()) {
            user.setStatus("ACTIVE");
            return;
        }
        if ("0".equals(s) || "DISABLED".equalsIgnoreCase(s)) {
            user.setStatus("DISABLED");
            return;
        }
        if ("1".equals(s) || "ACTIVE".equalsIgnoreCase(s)) {
            user.setStatus("ACTIVE");
        }
    }

    @Override
    public User getUserById(Long userId) {
        return userDAO.findById(userId);
    }

    @Override
    @Transactional
    public int updateUsername(Long userId, String username) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);

        int updated = userDAO.updateUsername(user);
        if (updated > 0) {
            log.info("更新用户名成功: userId={}, newUsername={}", userId, username);
        }
        return updated;
    }

    @Override
    @Transactional
    public int updatePassword(Long userId, String passwordHash) {
        User user = new User();
        user.setUserId(userId);
        user.setPasswordHash(passwordHash);

        int updated = userDAO.updatePassword(user);
        if (updated > 0) {
            log.info("更新密码成功: userId={}", userId);
        }
        return updated;
    }

    @Override
    @Transactional
    public int disableUser(Long userId) {
        int disabled = userDAO.disableUser(userId);
        if (disabled > 0) {
            log.info("禁用用户成功: userId={}", userId);
        }
        return disabled;
    }
}
