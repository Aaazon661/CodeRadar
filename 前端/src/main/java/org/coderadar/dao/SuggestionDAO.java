package org.coderadar.dao;

import org.apache.ibatis.annotations.*;
import org.coderadar.pojo.Suggestion;

import java.util.List;

@Mapper
public interface SuggestionDAO {

    //新增建议
    @Insert("""
            INSERT INTO suggestion(result_id, category, line_start, line_end,
                                   suggestion, severity)
            VALUES (#{resultId},#{category},#{lineStart},
                    #{lineEnd},#{suggestion},#{severity})
            """)
    @Options(useGeneratedKeys = true,keyProperty = "suggestionId")
    int insert(Suggestion suggestion);

    //批量新增建议
    @Insert("""
            <script>
            INSERT INTO suggestion
            (result_id, category, line_start, line_end,
             suggestion, severity)
            VALUES
            <foreach collection="list" item="item" separator=",">
                (#{item.resultId},
                 #{item.category},
                 #{item.lineStart},
                 #{item.lineEnd},
                 #{item.suggestion},
                 #{item.severity})
            </foreach>
            </script>
            """)
    int batchInsert(List<Suggestion> suggestions);

    //根据结果id查询建议列表
    @Select("SELECT * FROM suggestion WHERE result_id = #{resultId}")
    List<Suggestion> findByResultId(@Param("resultId") Long resultId);

    //更新建议内容
    @Update("UPDATE suggestion SET suggestion = #{suggestion} " +
            "WHERE suggestion_id = #{suggestionId}")
    int updateSuggestion(Suggestion suggestion);

    //更新建议起始行
    @Update("UPDATE suggestion SET line_start = #{lineStart} " +
            "WHERE suggestion_id = #{suggestionId}")
    int updateStart(Suggestion suggestion);

    //更新建议严重性
    @Update("UPDATE suggestion SET severity = #{severity} " +
            "WHERE suggestion_id = #{suggestionId}")
    int updateSeverity(Suggestion suggestion);

    //更新建议状态（ACCEPTED/IGNORED）
    @Update("UPDATE suggestion SET status = #{status} " +
            "WHERE suggestion_id = #{suggestionId}")
    int updateStatus(Suggestion suggestion);

    //删除某个结果的所有建议
    @Delete("DELETE FROM suggestion WHERE result_id = #{resultId}")
    int deleteByResultId(@Param("resultId") Long resultId); // 可选

    //删除建议
    @Delete("DELETE FROM suggestion WHERE suggestion_id = #{suggestionId}")
    int deleteBySuggestionId(@Param("suggestionId") Long suggestionId);

}
