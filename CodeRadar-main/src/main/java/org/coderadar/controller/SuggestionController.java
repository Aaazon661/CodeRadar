package org.coderadar.controller;

import org.coderadar.common.ResultResponse;
import org.coderadar.dto.suggestion.UpdateSuggestionContentDTO;
import org.coderadar.dto.suggestion.UpdateSuggestionLineStartDTO;
import org.coderadar.dto.suggestion.UpdateSuggestionSeverityDTO;
import org.coderadar.dto.suggestion.UpdateSuggestionStatusDTO;
import org.coderadar.pojo.Suggestion;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface SuggestionController {
    ResultResponse<List<Suggestion>> getByResultId(@RequestParam("resultId") Long resultId);
    ResultResponse<Void> updateContent(@PathVariable Long suggestionId, @RequestBody UpdateSuggestionContentDTO dto);
    ResultResponse<Void> updateLineStart(@PathVariable Long suggestionId, @RequestBody UpdateSuggestionLineStartDTO dto);
    ResultResponse<Void> updateSeverity(@PathVariable Long suggestionId, @RequestBody UpdateSuggestionSeverityDTO dto);
    ResultResponse<Void> updateStatus(@PathVariable Long suggestionId, @RequestBody UpdateSuggestionStatusDTO dto);
    ResultResponse<Void> deleteById(@PathVariable Long suggestionId);
    ResultResponse<Void> deleteByResultId(@PathVariable Long resultId);
}
