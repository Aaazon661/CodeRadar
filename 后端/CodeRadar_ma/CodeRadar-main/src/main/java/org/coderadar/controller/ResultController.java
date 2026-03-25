package org.coderadar.controller;

import org.coderadar.common.ResultResponse;
import org.coderadar.dto.result.UpdateSummaryDTO;
import org.coderadar.pojo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ResultController {
    ResultResponse<Result> getById(@PathVariable Long resultId);
    ResultResponse<Result> getByRequestId(@RequestParam("requestId") String requestId);
    ResultResponse<List<Result>> getByUserId(@RequestParam("userId") Long userId);
    ResultResponse<List<Result>> getByFileId(@RequestParam("fileId") Long fileId);
    ResultResponse<Void> updateSummary(@PathVariable Long resultId, @RequestBody UpdateSummaryDTO dto);
}
