package org.coderadar.pojo;

import lombok.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suggestion {
    private Long suggestionId;
    private Long resultId;
    private String category;   // SECURITY/PERFORMANCE/...
    private Integer lineStart; // 单行则 start=end
    private Integer lineEnd;
    private String suggestion;
    private String severity;   // LOW/MEDIUM/HIGH/CRITICAL
    private String status;     // NEW/EDITED/ACCEPTED/IGNORED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
