package org.coderadar.dao;

import org.apache.ibatis.annotations.*;
import org.coderadar.pojo.Result;

import java.util.List;

@Mapper
public interface ResultDAO {

    //新增结果
    @Insert("""
           INSERT INTO review_result(request_id, user_id, file_id,
           model, summary, raw_json)
           VALUES (#{requestId},#{userId},#{fileId},#{model},
            #{summary},#{rawJson})
           """)
    @Options(useGeneratedKeys = true,keyProperty = "resultId")
    int insert(Result result);

    //根据结果查询
    @Select("SELECT * FROM review_result WHERE result_id = #{resultId}")
    Result findById(@Param("resultId") Long resultId);

    //根据请求id查询
    @Select("SELECT * FROM review_result WHERE request_id = #{requestId}")
    Result findByRequestId(@Param("requestId") String requestId);

    //根据用户id查询列表
    @Select("SELECT * FROM review_result WHERE user_id = #{userId}")
    List<Result> findByUserId(@Param("userId") Long userId);

    //根据文件id查询列表
    @Select("SELECT * FROM review_result WHERE file_id = #{fileId}")
    List<Result> findByFileId(Long fileId);

    //更新summary
    @Update("UPDATE review_result SET summary = #{summary} WHERE result_id" +
            "= #{resultId}")
    int update(Result result);

}
