package org.coderadar.service;

import org.coderadar.pojo.Result;
import org.coderadar.pojo.UserFile;

public interface AImodelService {
    // 分析代码并返回AI的原始JSON响应
    String analyzeCode(String codeContent) throws Exception;

    // 调用AI进行代码审查，返回结构化的结果（已废弃，使用ReviewService.submitReview）
    @Deprecated
    Result reviewCode(UserFile file, String backgroundDescription);

    // 切换AI模型（例如：GPT/Qwen/Ollama）
    void switchAIModel(String modelName);

    // 获取当前使用的AI模型名称
    String getCurrentAIModel();
}