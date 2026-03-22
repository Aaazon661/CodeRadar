package org.coderadar.service;

import org.coderadar.pojo.Suggestion;
import java.util.List;

public interface SuggestionService {

    Suggestion add(Suggestion suggestion);

    int batchAdd(List<Suggestion> list);

    List<Suggestion> getByResultId(Long resultId);

    int updateContent(Long suggestionId, String content);

    int updateStart(Long suggestionId, Integer lineStart);

    int updateSeverity(Long suggestionId, String severity);

    int updateStatus(Long suggestionId, String status);

    int deleteByResultId(Long resultId);

    int deleteById(Long suggestionId);
}
