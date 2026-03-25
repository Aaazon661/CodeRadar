package org.coderadar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coderadar.dao.*;
import org.coderadar.pojo.*;
import org.coderadar.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        try {
            String content = readFileContent(file.getStoragePath());
            // 计算文件大小（如果未设置）
            if (file.getFileSize() == null && content != null) {
                file.setFileSize((long) content.length());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        try {
            String content = readFileContent(file.getStoragePath());

            if (file.getFileSize() == null && content != null) {
                file.setFileSize((long) content.length());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int result = fileDAO.update(file);
        if (result > 0) {
            log.info("更新文件成功: fileId={}", file.getFileId());
        }
        return result;
    }

    @Override
    @Transactional
    public String readFileContent(String filePath) throws IOException {
        try {
            // 尝试UTF-8编码读取
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // 如果UTF-8失败，尝试其他编码
            try {
                return new String(Files.readAllBytes(Paths.get(filePath)), "GBK");
            } catch (IOException e2) {
                // 如果都失败，使用ISO-8859-1（不会丢失数据）
                return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.ISO_8859_1);
            }
        }
    }

    @Override
    @Transactional
    public int save(String UPLOAD_DIR,String stored, MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Path target = Paths.get(UPLOAD_DIR, stored);
            Files.write(target, file.getBytes());
            System.out.println("写入成功，位置：" + target.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    @Transactional
    public int save(String stored, String content) {
        try {
            Path target = Paths.get(stored);
            Files.createDirectories(target.getParent());
            Files.write(target, content.getBytes());
            System.out.println("写入成功，位置：" + target.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }


}
