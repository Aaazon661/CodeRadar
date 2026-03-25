package org.coderadar.dto.review;

import lombok.Data;

@Data
public class ReviewSubmitDTO {
    private Long userId;
    private Long fileId;
    private String background;
    private String model;
}