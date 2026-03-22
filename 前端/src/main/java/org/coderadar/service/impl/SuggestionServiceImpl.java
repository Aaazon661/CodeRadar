package org.coderadar.service.impl;

import lombok.RequiredArgsConstructor;
import org.coderadar.dao.SuggestionDAO;
import org.coderadar.pojo.Suggestion;
import org.coderadar.service.SuggestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionServiceImpl implements SuggestionService {

    private final SuggestionDAO suggestionDAO;

    @Override
    public Suggestion add(Suggestion suggestion) {
        suggestionDAO.insert(suggestion);
        return suggestion;
    }

    @Override
    public int batchAdd(List<Suggestion> list) {
        if (list == null || list.isEmpty()) return 0;
        return suggestionDAO.batchInsert(list);
    }

    @Override
    public List<Suggestion> getByResultId(Long resultId) {
        return suggestionDAO.findByResultId(resultId);
    }

    @Override
    public int updateContent(Long suggestionId, String content) {
        Suggestion s = new Suggestion();
        s.setSuggestionId(suggestionId);
        s.setSuggestion(content);
        return suggestionDAO.updateSuggestion(s);
    }

    @Override
    public int updateStart(Long suggestionId, Integer lineStart) {
        Suggestion s = new Suggestion();
        s.setSuggestionId(suggestionId);
        s.setLineStart(lineStart);
        return suggestionDAO.updateStart(s);
    }

    @Override
    public int updateSeverity(Long suggestionId, String severity) {
        Suggestion s = new Suggestion();
        s.setSuggestionId(suggestionId);
        s.setSeverity(severity);
        return suggestionDAO.updateSeverity(s);
    }

    @Override
    public int updateStatus(Long suggestionId, String status) {
        Suggestion s = new Suggestion();
        s.setSuggestionId(suggestionId);
        s.setStatus(status);
        return suggestionDAO.updateStatus(s);
    }

    @Override
    public int deleteByResultId(Long resultId) {
        return suggestionDAO.deleteByResultId(resultId);
    }

    @Override
    public int deleteById(Long suggestionId) {
        return suggestionDAO.deleteBySuggestionId(suggestionId);
    }
}
