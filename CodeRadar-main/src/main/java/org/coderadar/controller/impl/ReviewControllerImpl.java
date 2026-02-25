package org.coderadar.controller.impl;

import lombok.RequiredArgsConstructor;
import org.coderadar.common.Code;
import org.coderadar.common.ResultResponse;
import org.coderadar.controller.ReviewController;
import org.coderadar.dto.review.ReviewSubmitDTO;
import org.coderadar.service.ReviewService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/submit")
    public ResultResponse<Long> submit(@RequestBody ReviewSubmitDTO dto) {
        if (dto == null || dto.getUserId() == null || dto.getUserId() <= 0
                || dto.getFileId() == null || dto.getFileId() <= 0
                || !StringUtils.hasText(dto.getModel())) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }

        try {
            Long resultId = reviewService.submitReview(
                    dto.getUserId(),
                    dto.getFileId(),
                    dto.getBackground(),
                    dto.getModel()
            );
            if (resultId == null) return ResultResponse.fail(Code.REVIEW_ERROR);
            return ResultResponse.success(resultId);
        } catch (Exception e) {
            return ResultResponse.fail(Code.REVIEW_ERROR.getCode(), "审查失败：" + e.getMessage());
        }
    }

    @PostMapping("/{resultId}/generate-file")
    public ResultResponse<Long> generateReviewedFile(@PathVariable Long resultId) {
        if (resultId == null || resultId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        try {
            Long newFileId = reviewService.generateReviewedFile(resultId);
            return newFileId != null ? ResultResponse.success(newFileId) : ResultResponse.fail(Code.NOT_FOUND);
        } catch (Exception e) {
            return ResultResponse.fail(Code.SERVER_ERROR.getCode(), "生成失败：" + e.getMessage());
        }
    }

    @PostMapping("/files/{newFileId}/confirm")
    public ResultResponse<Void> confirm(@PathVariable Long newFileId) {
        if (newFileId == null || newFileId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        try {
            reviewService.confirmReviewedFile(newFileId);
            return ResultResponse.success();
        } catch (Exception e) {
            return ResultResponse.fail(Code.SERVER_ERROR.getCode(), "确认失败：" + e.getMessage());
        }
    }

    @PostMapping("/files/{newFileId}/discard")
    public ResultResponse<Void> discard(@PathVariable Long newFileId) {
        if (newFileId == null || newFileId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        try {
            reviewService.discardReviewedFile(newFileId);
            return ResultResponse.success();
        } catch (Exception e) {
            return ResultResponse.fail(Code.SERVER_ERROR.getCode(), "放弃失败：" + e.getMessage());
        }
    }

    @GetMapping("/{resultId}/detail")
    public ResultResponse<Object> detail(@PathVariable Long resultId) {
        if (resultId == null || resultId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        Object vo = reviewService.getReviewDetail(resultId);
        return vo != null ? ResultResponse.success(vo) : ResultResponse.fail(Code.NOT_FOUND);
    }

    @GetMapping("/history")
    public ResultResponse<Object> historyByFile(@RequestParam("fileId") Long fileId) {
        if (fileId == null || fileId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        return ResultResponse.success(reviewService.getFileReviewHistory(fileId));
    }
}
