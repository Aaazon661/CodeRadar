package org.coderadar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coderadar.service.ResultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.coderadar.dao.ResultDAO;
import org.coderadar.pojo.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final ResultDAO resultDAO;

    @Override
    @Transactional
    public Result create(Result result) {
        // 补充属性赋值
        LocalDateTime now = LocalDateTime.now();

        if (result.getRequestId() == null) {
            result.setRequestId(UUID.randomUUID().toString());
        }
        if (result.getReviewTime() == null) {
            result.setReviewTime(now);
        }
        if (result.getCreatedAt() == null) {
            result.setCreatedAt(now);
        }
        if (result.getUpdatedAt() == null) {
            result.setUpdatedAt(now);
        }

        resultDAO.insert(result);
        log.info("创建审查结果: resultId={}, requestId={}", result.getResultId(), result.getRequestId());
        return result;
    }

    @Override
    public Result getById(Long resultId) {
        return resultDAO.findById(resultId);
    }

    @Override
    public Result getByRequestId(String requestId) {
        return resultDAO.findByRequestId(requestId);
    }

    @Override
    public List<Result> getByUserId(Long userId) {
        return resultDAO.findByUserId(userId);
    }

    @Override
    public List<Result> getByFileId(Long fileId) {
        return resultDAO.findByFileId(fileId);
    }

    @Override
    @Transactional
    public int updateSummary(Long resultId, String summary) {
        Result result = new Result();
        result.setResultId(resultId);
        result.setSummary(summary);
        result.setUpdatedAt(LocalDateTime.now());

        int updated = resultDAO.update(result);
        if (updated > 0) {
            log.info("更新审查摘要: resultId={}", resultId);
        }
        return updated;
    }
}
