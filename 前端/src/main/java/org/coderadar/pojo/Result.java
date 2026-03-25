package org.coderadar.pojo;

import lombok.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {
    private Long resultId;
    /** 一次模型调用/审查的请求ID */
    private String requestId;
    private Long userId;
    private Long fileId;
    private String model;
    private LocalDateTime reviewTime;
    private String summary;
    /** 可选：保存AI原始返回，便于调试/复现 */
    private String rawJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
