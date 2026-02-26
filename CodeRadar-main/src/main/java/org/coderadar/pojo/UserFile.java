package org.coderadar.pojo;

import lombok.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFile {
    private Long fileId;
    private Long userId;
    /** 原始文件名（用户上传看到的） */
    private String originalFileName;
    /** 系统内部保存名/展示名 */
    private String storedFileName;
    /** 文件存储路径/对象存储key */
    private String storagePath;
    /** 文件类型：java/py/js/txt... */
    private String fileType;
    /** 文件大小（字节） */
    private Long fileSize;
    /** 文件hash（用于去重/校验，可选但推荐） */
    private String fileHash;
    /** 编码：UTF-8/GBK... */
    private String encoding;
    /** 换行符：LF/CRLF */
    private String newline;
    /** 软删除 */
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
