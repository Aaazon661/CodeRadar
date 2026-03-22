package org.coderadar.dao;

import org.apache.ibatis.annotations.*;
import org.coderadar.pojo.User;

@Mapper
public interface UserDAO {
    // 表名 user 为 MySQL 保留字，必须用反引号，否则易触发 SQL 语法错误导致 500
    @Insert("INSERT INTO `user`(account, username, password_hash, salt) " +
            "VALUES(#{account}, #{username}, #{passwordHash}, #{salt})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insertUser(User user);

    @Select("SELECT * FROM `user` WHERE account = #{account}")
    User findByAccount(String account);

    @Select("SELECT * FROM `user` WHERE user_id = #{userId}")
    User findById(Long userId);

    @Update("UPDATE `user` SET last_login_at = NOW() WHERE user_id = #{userId}")
    void updateLastLoginTime(Long userId);

    @Update("UPDATE `user` SET username = #{username}, updated_at = NOW() WHERE user_id = #{userId}")
    int updateUsername(User user);

    @Update("UPDATE `user` SET password_hash = #{passwordHash}, updated_at = NOW() WHERE user_id = #{userId}")
    int updatePassword(User user);

    @Update("UPDATE `user` SET status = 'DISABLED', updated_at = NOW() WHERE user_id = #{userId}")
    int disableUser(Long userId);
}
