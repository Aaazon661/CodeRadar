package org.coderadar.pojo;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {
    private Long suggestionId;
    private Long resultId;
    private String requestId; // 冗余字段，方便查询
    private String category; // 建议类型 (如: 安全性, 性能, 可读性)
    private String lineNumbers; // 行号 (如: "10-15")
    private String suggestion; // 具体建议内容
    private String severity; // 严重程度 (HIGH, MEDIUM, LOW)
    private Integer lineStart; // 起始行号
    private String status; // 状态 (PENDING, RESOLVED, IGNORED)
    private LocalDateTime createdAt;
}
