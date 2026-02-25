package org.coderadar.controller.impl;

import lombok.RequiredArgsConstructor;
import org.coderadar.common.Code;
import org.coderadar.common.ResultResponse;
import org.coderadar.controller.ResultController;
import org.coderadar.dto.result.UpdateSummaryDTO;
import org.coderadar.pojo.Result;
import org.coderadar.service.ResultService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultControllerImpl implements ResultController {

    private final ResultService resultService;

    @GetMapping("/{resultId}")
    public ResultResponse<Result> getById(@PathVariable Long resultId) {
        if (resultId == null || resultId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        Result r = resultService.getById(resultId);
        return r != null ? ResultResponse.success(r) : ResultResponse.fail(Code.NOT_FOUND);
    }

    @GetMapping("/by-request")
    public ResultResponse<Result> getByRequestId(@RequestParam("requestId") String requestId) {
        if (!StringUtils.hasText(requestId)) return ResultResponse.fail(Code.PARAM_ERROR);
        Result r = resultService.getByRequestId(requestId);
        return r != null ? ResultResponse.success(r) : ResultResponse.fail(Code.NOT_FOUND);
    }

    @GetMapping("/by-user")
    public ResultResponse<List<Result>> getByUserId(@RequestParam("userId") Long userId) {
        if (userId == null || userId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        return ResultResponse.success(resultService.getByUserId(userId));
    }

    @GetMapping("/by-file")
    public ResultResponse<List<Result>> getByFileId(@RequestParam("fileId") Long fileId) {
        if (fileId == null || fileId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        return ResultResponse.success(resultService.getByFileId(fileId));
    }

    @PutMapping("/{resultId}/summary")
    public ResultResponse<Void> updateSummary(@PathVariable Long resultId, @RequestBody UpdateSummaryDTO dto) {
        if (resultId == null || resultId <= 0 || dto == null || !StringUtils.hasText(dto.getSummary())) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }
        int rows = resultService.updateSummary(resultId, dto.getSummary());
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.NOT_FOUND);
    }
}
