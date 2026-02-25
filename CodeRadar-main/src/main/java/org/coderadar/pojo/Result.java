package org.coderadar.pojo;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Long resultId;
    private String requestId; // AI 返回的唯一请求 ID
    private Long userId;
    private Long fileId;
    private String model; // AI 模型名称
    private LocalDateTime reviewTime;
    private String summary; // 审查总结
    private String rawJson; // 原始 AI 返回的完整 JSON (可选，用于调试)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
