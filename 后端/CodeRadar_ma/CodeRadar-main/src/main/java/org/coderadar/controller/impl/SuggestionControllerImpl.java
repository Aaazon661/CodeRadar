package org.coderadar.controller.impl;

import lombok.RequiredArgsConstructor;
import org.coderadar.common.Code;
import org.coderadar.common.ResultResponse;
import org.coderadar.controller.SuggestionController;
import org.coderadar.dto.suggestion.UpdateSuggestionContentDTO;
import org.coderadar.dto.suggestion.UpdateSuggestionLineStartDTO;
import org.coderadar.dto.suggestion.UpdateSuggestionSeverityDTO;
import org.coderadar.dto.suggestion.UpdateSuggestionStatusDTO;
import org.coderadar.pojo.Suggestion;
import org.coderadar.service.SuggestionService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class SuggestionControllerImpl implements SuggestionController {

    private final SuggestionService suggestionService;

    @GetMapping("/by-result")
    public ResultResponse<List<Suggestion>> getByResultId(@RequestParam("resultId") Long resultId) {
        if (resultId == null || resultId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        return ResultResponse.success(suggestionService.getByResultId(resultId));
    }

    @PutMapping("/{suggestionId}/content")
    public ResultResponse<Void> updateContent(@PathVariable Long suggestionId,
                                              @RequestBody UpdateSuggestionContentDTO dto) {
        if (suggestionId == null || suggestionId <= 0 || dto == null || !StringUtils.hasText(dto.getContent())) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }
        int rows = suggestionService.updateContent(suggestionId, dto.getContent());
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.NOT_FOUND);
    }

    @PutMapping("/{suggestionId}/line-start")
    public ResultResponse<Void> updateLineStart(@PathVariable Long suggestionId,
                                                @RequestBody UpdateSuggestionLineStartDTO dto) {
        if (suggestionId == null || suggestionId <= 0 || dto == null || dto.getLineStart() == null || dto.getLineStart() <= 0) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }
        int rows = suggestionService.updateStart(suggestionId, dto.getLineStart());
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.NOT_FOUND);
    }

    @PutMapping("/{suggestionId}/severity")
    public ResultResponse<Void> updateSeverity(@PathVariable Long suggestionId,
                                               @RequestBody UpdateSuggestionSeverityDTO dto) {
        if (suggestionId == null || suggestionId <= 0 || dto == null || !StringUtils.hasText(dto.getSeverity())) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }
        int rows = suggestionService.updateSeverity(suggestionId, dto.getSeverity());
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.NOT_FOUND);
    }

    @PutMapping("/{suggestionId}/status")
    public ResultResponse<Void> updateStatus(@PathVariable Long suggestionId,
                                             @RequestBody UpdateSuggestionStatusDTO dto) {
        if (suggestionId == null || suggestionId <= 0 || dto == null || !StringUtils.hasText(dto.getStatus())) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }
        int rows = suggestionService.updateStatus(suggestionId, dto.getStatus());
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.NOT_FOUND);
    }

    @DeleteMapping("/{suggestionId}")
    public ResultResponse<Void> deleteById(@PathVariable Long suggestionId) {
        if (suggestionId == null || suggestionId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        int rows = suggestionService.deleteById(suggestionId);
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.NOT_FOUND);
    }

    @DeleteMapping("/by-result/{resultId}")
    public ResultResponse<Void> deleteByResultId(@PathVariable Long resultId) {
        if (resultId == null || resultId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        int rows = suggestionService.deleteByResultId(resultId);
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.NOT_FOUND);
    }
}
