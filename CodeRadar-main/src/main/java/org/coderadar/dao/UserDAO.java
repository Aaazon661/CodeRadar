package org.coderadar.dao;

import org.apache.ibatis.annotations.*;
import org.coderadar.pojo.User;

@Mapper
public interface UserDAO {
    @Insert("INSERT INTO user(account, username, password_hash, salt) " +
            "VALUES(#{account}, #{username}, #{passwordHash}, #{salt})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insertUser(User user);

    @Select("SELECT * FROM user WHERE account = #{account}")
    User findByAccount(String account);

    void insert(User user);

    @Update("UPDATE user SET last_login_at = NOW() WHERE user_id = #{userId}")
    void updateLastLoginTime(Long userId);

    User findById(Long userId);

    int updateUsername(User user);

    int updatePassword(User user);

    int disableUser(Long userId);
}
