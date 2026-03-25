package org.coderadar.dao;

import org.apache.ibatis.annotations.*;
import org.coderadar.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface UserDAO {

//    @Insert("INSERT INTO user(account,username,password_hash) VALUES ('test05','test','test')")
//    public void testAddUser();

    //新增用户，获取自增id存入对象
    @Insert("INSERT INTO user(account,username,password_hash) VALUES " +
            "(#{account},#{username},#{passwordHash})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    //根据id搜索用户
    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User findById(@Param("userId") Long userId);

    //根据账户搜索用户
    @Select("SELECT * FROM user WHERE account = #{account}")
    User findByAccount(@Param("account") String account);

    //获取全部用户（！管理员权限未实现）
    @Select("SELECT * FROM user")
    List<User> findAll();

    //更新用户名
    @Update("UPDATE user SET username = #{username} WHERE user_id = #{userId}")
    int updateUsername(User user);

    //更新密码
    @Update("UPDATE user SET password_hash = #{passwordHash} WHERE user_id = #{userId}")
    int updatePassword(User user);

    //更新最后登录时间
    @Update("UPDATE user SET last_login_at = CURRENT_TIMESTAMP WHERE user_id = #{userId}")
    int updateLastLoginTime(@Param("userId") Long userId);

    //封禁用户
    @Update("UPDATE user SET status = 'DISABLE' WHERE user_id = #{userId}")
    int disableUser(@Param("userId") Long userId);
}
