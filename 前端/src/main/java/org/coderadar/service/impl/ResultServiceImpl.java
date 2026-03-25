package org.coderadar.service.impl;

import lombok.RequiredArgsConstructor;
import org.coderadar.dao.ResultDAO;
import org.coderadar.pojo.Result;
import org.coderadar.service.ResultService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final ResultDAO resultDAO;

    @Override
    public Result create(Result result) {
        resultDAO.insert(result);
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
    public int updateSummary(Long resultId, String summary) {
        Result result = new Result();
        result.setResultId(resultId);
        result.setSummary(summary);
        return resultDAO.update(result);
    }
}
