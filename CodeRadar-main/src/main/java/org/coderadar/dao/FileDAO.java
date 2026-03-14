package org.coderadar.dao;

import org.apache.ibatis.annotations.*;
import org.coderadar.pojo.UserFile;

import java.util.List;

@Mapper
public interface FileDAO {
    @Insert("INSERT INTO user_file(user_id, original_file_name, stored_file_name, storage_path," +
            "file_type,file_size,encoding) " +
            "VALUES(#{userId}, #{originalFileName}, #{storedFileName}, #{storagePath},#{fileType},#{fileSize},'UTF-8')")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    void insertFile(UserFile file);

    @Select("SELECT * FROM user_file WHERE file_id = #{fileId}")
    UserFile findById(Long fileId);

    // 通过文件ID查询文件
    @Select("SELECT * FROM user_file WHERE file_id = #{fileId}")
    UserFile findByFileId(Long fileId);

    // 查询用户的所有文件（未删除）
    @Select("SELECT * FROM user_file WHERE user_id = #{userId} AND deleted = FALSE")
    List<UserFile> findByUserId(Long userId);

    // 软删除文件
    @Update("UPDATE user_file SET deleted = TRUE, updated_at = NOW() WHERE file_id = #{fileId}")
    int softDelete(Long fileId);

    // 更新文件信息
    @Update("UPDATE user_file SET " +
            "original_file_name = #{originalFileName}, " +
            "stored_file_name = #{storedFileName}, " +
            "storage_path = #{storagePath}, " +
            //"file_content = #{fileContent}, " +
            "file_type = #{fileType}, " +
            "file_size = #{fileSize}, " +
            "file_hash = #{fileHash}, " +
            "encoding = #{encoding}, " +
            "newline = #{newline}, " +
            "deleted = #{deleted}, " +
            "updated_at = #{updatedAt} " +
            "WHERE file_id = #{fileId}")
    int update(UserFile file);
}
