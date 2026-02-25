package org.coderadar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coderadar.dao.*;
import org.coderadar.pojo.*;
import org.coderadar.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileDAO fileDAO;

    @Override
    @Transactional
    public UserFile upload(UserFile file) {
        // 补充属性赋值
        LocalDateTime now = LocalDateTime.now();
        if (file.getCreatedAt() == null) {
            file.setCreatedAt(now);
        }
        if (file.getUpdatedAt() == null) {
            file.setUpdatedAt(now);
        }
        if (file.getDeleted() == null) {
            file.setDeleted(false);
        }

        // 计算文件大小（如果未设置）
        if (file.getFileSize() == null && file.getFileContent() != null) {
            file.setFileSize((long) file.getFileContent().length());
        }

        // 使用带注解的方法执行插入，避免 MyBatis 找不到 XML 映射
        fileDAO.insertFile(file);
        log.info("上传文件成功: fileId={}, fileName={}", file.getFileId(), file.getOriginalFileName());
        return file;
    }

    @Override
    public UserFile getById(Long fileId) {
        return fileDAO.findByFileId(fileId);
    }

    @Override
    public List<UserFile> getUserFiles(Long userId) {
        return fileDAO.findByUserId(userId);
    }

    @Override
    @Transactional
    public int delete(Long fileId) {
        int result = fileDAO.softDelete(fileId);
        if (result > 0) {
            log.info("删除文件成功: fileId={}", fileId);
        }
        return result;
    }

    @Override
    @Transactional
    public int update(UserFile file) {
        // 更新时间戳
        file.setUpdatedAt(LocalDateTime.now());

        // 如果更新了文件内容，重新计算文件大小
        if (file.getFileContent() != null) {
            file.setFileSize((long) file.getFileContent().length());
        }

        int result = fileDAO.update(file);
        if (result > 0) {
            log.info("更新文件成功: fileId={}", file.getFileId());
        }
        return result;
    }
}
