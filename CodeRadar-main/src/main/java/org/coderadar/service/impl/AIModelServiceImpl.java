package org.coderadar.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.coderadar.pojo.Result;
import org.coderadar.pojo.UserFile;
import org.coderadar.service.AImodelService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class AIModelServiceImpl implements AImodelService {

    @Value("${ai.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.model}")
    private String apiModel;

    /**
     * AI 接口调用超时时间（毫秒），可在 application.yml 中通过 ai.api.timeoutMillis 覆盖
     * 示例：
     * ai:
     *   api:
     *     timeoutMillis: 120000
     */
    @Value("${ai.api.timeoutMillis:120000}")
    private long timeoutMillis;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout( timeoutMillis, TimeUnit.MILLISECONDS)
            .writeTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
            .callTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分析代码并返回AI的原始JSON响应
     */
    @Override
    public String analyzeCode(String codeContent) throws Exception {
        String prompt = buildPrompt(codeContent);
        String requestBody = buildRequestBody(prompt);

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("AI API 调用失败: " + response.code());
            }

            String responseBody = response.body().string();
            return extractContent(responseBody);
        }
    }

    private String buildPrompt(String code) {
        return "你是一个专业的代码审计专家。请分析以下代码，并严格按照以下 JSON 格式返回结果（不要包含 Markdown 标记如 ```json）：\n" +
                "{\n" +
                "  \"requestId\": \"唯一ID\",\n" +
                "  \"model\": \"模型名称\",\n" +
                "  \"reviewTime\": \"2024-01-01T10:00:00\",\n" +
                "  \"summary\": \"总体评价\",\n" +
                "  \"suggestions\": [\n" +
                "    {\"category\": \"安全性\", \"lineNumbers\": \"10-15\", \"suggestion\": \"建议内容\", \"severity\": \"HIGH\"}\n" +
                "  ]\n" +
                "}\n\n" +
                "代码内容：\n" + code;
    }

    private String buildRequestBody(String prompt) throws Exception {
        String body = "{\n" +
                "  \"model\": \"" + apiModel + "\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"你是代码审计专家，必须返回纯 JSON 格式结果\"},\n" +
                "    {\"role\": \"user\", \"content\": " + objectMapper.writeValueAsString(prompt) + "}\n" +
                "  ],\n" +
                "  \"temperature\": 0.7\n" +
                "}";
        return body;
    }

    private String extractContent(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        String content = root.at("/choices/0/message/content").asText();

        // 清理可能的 Markdown 代码块标记
        content = content.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7);
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3);
        }
        return content.trim();
    }

    /**
     * 审查代码并返回结构化的Result对象
     * 注意：此方法已被ReviewService.submitReview替代，建议废弃
     */
    @Override
    @Deprecated
    public Result reviewCode(UserFile file, String backgroundDescription) {
        // TODO: 此方法应该被ReviewService.submitReview替代
        throw new UnsupportedOperationException("请使用 ReviewService.submitReview 方法");
    }

    /**
     * 切换AI模型
     * TODO: 实现模型切换逻辑，支持多种AI模型（GPT/Qwen/Ollama等）
     */
    @Override
    public void switchAIModel(String modelName) {
        // TODO: 实现模型切换逻辑
        // 1. 验证模型名称是否支持
        // 2. 更新apiUrl和相关配置
        // 3. 可能需要更新prompt模板
        throw new UnsupportedOperationException("模型切换功能尚未实现");
    }

    /**
     * 获取当前使用的AI模型名称
     */
    @Override
    public String getCurrentAIModel() {
        // 从配置中提取模型名称，默认返回deepseek-chat
        return "deepseek-chat";
    }
}
