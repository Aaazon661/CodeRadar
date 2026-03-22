package org.coderadar.dao;

import org.apache.ibatis.annotations.*;
import org.coderadar.pojo.Result;

import java.util.List;

@Mapper
public interface ResultDAO {
    @Insert("INSERT INTO review_result(request_id, user_id, file_id, model, summary, review_time, raw_json) " +
            "VALUES(#{requestId}, #{userId}, #{fileId}, #{model}, #{summary}, #{reviewTime}, #{rawJson})")
    @Options(useGeneratedKeys = true, keyProperty = "resultId")
    void insertResult(Result result);

    @Select("SELECT * FROM review_result WHERE request_id = #{requestId}")
    Result findByRequestId(String requestId);

    // 通过主键插入结果（统一使用带注解的方法）
    @Insert("INSERT INTO review_result(request_id, user_id, file_id, model, summary, review_time, raw_json, created_at, updated_at) " +
            "VALUES(#{requestId}, #{userId}, #{fileId}, #{model}, #{summary}, #{reviewTime}, #{rawJson}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "resultId")
    void insert(Result result);

    // 根据ID查询
    @Select("SELECT * FROM review_result WHERE result_id = #{resultId}")
    Result findById(Long resultId);

    // 根据用户查询
    @Select("SELECT * FROM review_result WHERE user_id = #{userId} ORDER BY review_time DESC")
    List<Result> findByUserId(Long userId);

    // 根据文件查询
    @Select("SELECT * FROM review_result WHERE file_id = #{fileId} ORDER BY review_time DESC")
    List<Result> findByFileId(Long fileId);

    // 更新记录
    @Update("UPDATE review_result SET " +
            "summary = #{summary}, " +
            "review_time = #{reviewTime}, " +
            "raw_json = #{rawJson}, " +
            "updated_at = #{updatedAt} " +
            "WHERE result_id = #{resultId}")
    int update(Result result);
}
