package org.coderadar.service.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coderadar.dao.SuggestionDAO;
import org.coderadar.pojo.Suggestion;
import org.coderadar.service.SuggestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuggestionServiceImpl implements SuggestionService {

    private final SuggestionDAO suggestionDAO;

    @Override
    @Transactional
    public Suggestion add(Suggestion suggestion) {
        // 补充属性赋值
        if (suggestion.getCreatedAt() == null) {
            suggestion.setCreatedAt(LocalDateTime.now());
        }
        if (suggestion.getStatus() == null) {
            suggestion.setStatus("PENDING");
        }

        suggestionDAO.insert(suggestion);
        log.info("添加建议: suggestionId={}, resultId={}", suggestion.getSuggestionId(), suggestion.getResultId());
        return suggestion;
    }

    @Override
    @Transactional
    public int batchAdd(List<Suggestion> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        // 批量补充属性
        LocalDateTime now = LocalDateTime.now();
        for (Suggestion suggestion : list) {
            if (suggestion.getCreatedAt() == null) {
                suggestion.setCreatedAt(now);
            }
            if (suggestion.getStatus() == null) {
                suggestion.setStatus("PENDING");
            }
        }

        int count = suggestionDAO.batchInsert(list);
        log.info("批量添加建议: count={}", count);
        return count;
    }

    @Override
    public List<Suggestion> getByResultId(Long resultId) {
        return suggestionDAO.findByResultId(resultId);
    }

    @Override
    @Transactional
    public int updateContent(Long suggestionId, String content) {
        Suggestion s = new Suggestion();
        s.setSuggestionId(suggestionId);
        s.setSuggestion(content);

        int updated = suggestionDAO.updateSuggestion(s);
        if (updated > 0) {
            log.info("更新建议内容: suggestionId={}", suggestionId);
        }
        return updated;
    }

    @Override
    @Transactional
    public int updateStart(Long suggestionId, Integer lineStart) {
        Suggestion s = new Suggestion();
        s.setSuggestionId(suggestionId);
        s.setLineStart(lineStart);

        int updated = suggestionDAO.updateStart(s);
        if (updated > 0) {
            log.info("更新建议起始行: suggestionId={}, lineStart={}", suggestionId, lineStart);
        }
        return updated;
    }

    @Override
    @Transactional
    public int updateSeverity(Long suggestionId, String severity) {
        Suggestion s = new Suggestion();
        s.setSuggestionId(suggestionId);
        s.setSeverity(severity);

        int updated = suggestionDAO.updateSeverity(s);
        if (updated > 0) {
            log.info("更新建议严重程度: suggestionId={}, severity={}", suggestionId, severity);
        }
        return updated;
    }

    @Override
    @Transactional
    public int updateStatus(Long suggestionId, String status) {
        Suggestion s = new Suggestion();
        s.setSuggestionId(suggestionId);
        s.setStatus(status);

        int updated = suggestionDAO.updateStatus(s);
        if (updated > 0) {
            log.info("更新建议状态: suggestionId={}, status={}", suggestionId, status);
        }
        return updated;
    }

    @Override
    @Transactional
    public int deleteByResultId(Long resultId) {
        int deleted = suggestionDAO.deleteByResultId(resultId);
        if (deleted > 0) {
            log.info("删除结果的所有建议: resultId={}, count={}", resultId, deleted);
        }
        return deleted;
    }

    @Override
    @Transactional
    public int deleteById(Long suggestionId) {
        int deleted = suggestionDAO.deleteBySuggestionId(suggestionId);
        if (deleted > 0) {
            log.info("删除建议: suggestionId={}", suggestionId);
        }
        return deleted;
    }
}
