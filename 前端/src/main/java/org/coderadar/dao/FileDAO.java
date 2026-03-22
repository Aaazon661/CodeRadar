package org.coderadar.dao;

import org.apache.ibatis.annotations.*;
import org.coderadar.pojo.UserFile;

import java.util.List;

@Mapper
public interface FileDAO {

    //新增文件
    @Insert("""
    INSERT INTO user_file
    (user_id, original_file_name, stored_file_name, storage_path, 
     file_type, file_size, file_hash, encoding, newline) 
    VALUES (#{userId},#{originalFileName}, 
            #{storedFileName},#{storagePath},#{fileType},#{fileSize}, 
            #{fileHash},#{encoding},#{newline})
    """)
    @Options(useGeneratedKeys = true,keyProperty = "fileId")
    int insert(UserFile file);

    //根据文件id搜索文件
    @Select("SELECT * FROM user_file WHERE file_id = #{fileId}")
    UserFile findByFileId(@Param("fileId") Long fileId);

    //根据用户id搜索文件（列表）
    @Select("SELECT * FROM user_file WHERE user_id = #{userId}")
    List<UserFile> findByUserId(@Param("userId") Long userId);

    //软删除，将deleted字段修改为1（删除有效）
    @Update("UPDATE user_file SET deleted = 1 WHERE file_id = #{fileId}")
    int softDelete(@Param("fileId") Long fileId);

    //更新文件（原始文件名、大小、更新时间）
    @Update("""
    UPDATE user_file SET original_file_name = #{originalFileName},
                     file_size = #{fileSize},file_hash = #{fileHash},
                    updated_at = CURRENT_TIMESTAMP 
                   WHERE file_id = #{fileId}
    """)
    int update(UserFile file);

    //根据文件哈希搜索文件，暂不实现
    UserFile findByHash(String fileHash);

}
