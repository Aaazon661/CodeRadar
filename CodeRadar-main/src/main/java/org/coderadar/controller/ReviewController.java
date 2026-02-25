package org.coderadar.controller;

import org.coderadar.common.ResultResponse;
import org.coderadar.dto.review.ReviewSubmitDTO;
import org.springframework.web.bind.annotation.*;

public interface ReviewController {
    ResultResponse<Long> submit(@RequestBody ReviewSubmitDTO dto);
    ResultResponse<Long> generateReviewedFile(@PathVariable Long resultId);
    ResultResponse<Void> confirm(@PathVariable Long newFileId);
    ResultResponse<Void> discard(@PathVariable Long newFileId);
    ResultResponse<Object> detail(@PathVariable Long resultId);
    ResultResponse<Object> historyByFile(@RequestParam("fileId") Long fileId);
}
