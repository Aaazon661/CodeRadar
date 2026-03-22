package org.coderadar.service;

import org.coderadar.pojo.Result;

import java.util.List;

public interface ResultService {
    Result create(Result result);

    Result getById(Long resultId);

    Result getByRequestId(String requestId);

    List<Result> getByUserId(Long userId);

    List<Result> getByFileId(Long fileId);

    int updateSummary(Long resultId, String summary);

}
