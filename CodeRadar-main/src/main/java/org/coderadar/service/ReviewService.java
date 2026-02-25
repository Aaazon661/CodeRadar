package org.coderadar.service;

public interface ReviewService {
    // 提交代码审查请求
    Long submitReview(Long userId, Long fileId, String background, String model);

    // 生成审查后的文件
    Long generateReviewedFile(Long resultId);

    // 获取审查详情（包含Result及关联的Suggestion列表）
    Object getReviewDetail(Long resultId);

    // 获取文件的审查历史
    Object getFileReviewHistory(Long fileId);

    // 确认审查后的文件（可能涉及将生成的注释写回原文件或生成新版本）
    void confirmReviewedFile(Long newFileId) throws Exception;

    // 放弃审查结果
    void discardReviewedFile(Long newFileId) throws Exception;
}