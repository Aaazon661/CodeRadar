package org.coderadar.dao;

import org.apache.ibatis.annotations.*;
import org.coderadar.pojo.Suggestion;
import java.util.List;

@Mapper
public interface SuggestionDAO {
    @Insert("INSERT INTO suggestion(result_id, request_id, category, line_numbers, suggestion, severity, created_at) " +
            "VALUES(#{resultId}, #{requestId}, #{category}, #{lineNumbers}, #{suggestion}, #{severity}, #{createdAt})")
    void insertSuggestion(Suggestion suggestion);

    @Select("SELECT * FROM suggestion WHERE result_id = #{resultId}")
    List<Suggestion> findByResultId(Long resultId);

    // 单条插入，统一使用带注解的方法
    @Insert("INSERT INTO suggestion(result_id, request_id, category, line_numbers, suggestion, severity, created_at) " +
            "VALUES(#{resultId}, #{requestId}, #{category}, #{lineNumbers}, #{suggestion}, #{severity}, #{createdAt})")
    void insert(Suggestion suggestion);

    // 批量插入，使用 foreach
    @Insert({
            "<script>",
            "INSERT INTO suggestion(result_id, request_id, category, line_numbers, suggestion, severity, created_at) VALUES",
            "<foreach collection='list' item='s' separator=','>",
            "(",
            "#{s.resultId},",
            "#{s.requestId},",
            "#{s.category},",
            "#{s.lineNumbers},",
            "#{s.suggestion},",
            "#{s.severity},",
            "#{s.createdAt}",
            ")",
            "</foreach>",
            "</script>"
    })
    int batchInsert(@Param("list") List<Suggestion> list);

    @Update("UPDATE suggestion SET " +
            "category = #{category}, " +
            "line_numbers = #{lineNumbers}, " +
            "suggestion = #{suggestion}, " +
            "severity = #{severity} " +
            "WHERE suggestion_id = #{suggestionId}")
    int updateSuggestion(Suggestion s);

    @Update("UPDATE suggestion SET line_numbers = #{lineNumbers} WHERE suggestion_id = #{suggestionId}")
    int updateStart(Suggestion s);

    @Update("UPDATE suggestion SET severity = #{severity} WHERE suggestion_id = #{suggestionId}")
    int updateSeverity(Suggestion s);

    @Update("UPDATE suggestion SET status = #{status} WHERE suggestion_id = #{suggestionId}")
    int updateStatus(Suggestion s);

    @Delete("DELETE FROM suggestion WHERE result_id = #{resultId}")
    int deleteByResultId(Long resultId);

    @Delete("DELETE FROM suggestion WHERE suggestion_id = #{suggestionId}")
    int deleteBySuggestionId(Long suggestionId);
}
