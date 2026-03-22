package org.coderadar.controller.impl;

import lombok.RequiredArgsConstructor;
import org.coderadar.common.Code;
import org.coderadar.common.ResultResponse;
import org.coderadar.controller.FileController;
import org.coderadar.pojo.Result;
import org.coderadar.pojo.UserFile;
import org.coderadar.service.FileService;
import org.coderadar.service.ReviewService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileControllerImpl implements FileController {

    private final FileService fileService;
    private final ReviewService reviewService;

    // 可迁移到 application.yml
    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/upload")
    public ResultResponse<Object> upload(@RequestParam("userId") Long userId,
                                         @RequestParam("file") MultipartFile file) {
        if (userId == null || userId <= 0 || file == null || file.isEmpty()) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }

        try {


            String original = file.getOriginalFilename();
            String stored = UUID.randomUUID() + "_" + (StringUtils.hasText(original) ? original : "file.txt");
            fileService.save(UPLOAD_DIR,stored,file);


            String fileType = "";
            if (StringUtils.hasText(original) && original.contains(".")) {
                fileType = original.substring(original.lastIndexOf('.') + 1);
            }

            // 读取文件内容
            //String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            UserFile uf = UserFile.builder()
                    .userId(userId)
                    .originalFileName(original)
                    .storedFileName(stored)
                    .storagePath(Paths.get(UPLOAD_DIR, stored).toString())
                    //.fileContent(content)  // 保存文件内容
                    .fileType(fileType)
                    .fileSize(file.getSize())
                    .deleted(false)
                    .build();

            UserFile saved = fileService.upload(uf);

            // 自动触发 AI 审查
            try {
                Long resultId = reviewService.submitReview(userId, saved.getFileId(), null, "deepseek-chat");

                // 获取审查结果详情
                Object reviewDetail = reviewService.getReviewDetail(resultId);

                return ResultResponse.success(reviewDetail);
            } catch (Exception e) {
                // 如果 AI 审查失败，仍然返回文件上传成功的信息
                return ResultResponse.success(java.util.Map.of(
                    "file", saved,
                    "reviewError", "AI 审查失败: " + e.getMessage()
                ));
            }
        } catch (Exception e) {
            return ResultResponse.fail(Code.SERVER_ERROR.getCode(), "上传失败：" + e.getMessage());
        }
    }

    @GetMapping("/{fileId}")
    public ResultResponse<UserFile> getById(@PathVariable Long fileId) {
        if (fileId == null || fileId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);

        UserFile file = fileService.getById(fileId);
        if (file == null) return ResultResponse.fail(Code.FILE_NOT_FOUND);
        try {
            file.setFileContent(fileService.readFileContent(file.getStoragePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResultResponse.success(file);
    }

    @GetMapping
    public ResultResponse<List<UserFile>> listByUser(@RequestParam("userId") Long userId) {
        if (userId == null || userId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        return ResultResponse.success(fileService.getUserFiles(userId));
    }

    @DeleteMapping("/{fileId}")
    public ResultResponse<Void> delete(@PathVariable Long fileId) {
        if (fileId == null || fileId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        int rows = fileService.delete(fileId);
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.FILE_NOT_FOUND);
    }

    /**
     * 下载文件：根据 fileId 以附件形式返回文件内容
     * 目前优先使用数据库中的 fileContent，如果为空则尝试从 storagePath 读取
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> download(@PathVariable Long fileId) {
        if (fileId == null || fileId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        UserFile file = fileService.getById(fileId);
        if (file == null || Boolean.TRUE.equals(file.getDeleted())) {
            return ResponseEntity.notFound().build();
        }

        try {
            String content = fileService.readFileContent(file.getStoragePath());
            byte[] contentBytes;
            if (content != null) {
                contentBytes = content.getBytes(StandardCharsets.UTF_8);
            } else if (file.getStoragePath() != null) {
                Path path = Paths.get(file.getStoragePath());
                contentBytes = Files.readAllBytes(path);
            } else {
                return ResponseEntity.noContent().build();
            }

            String downloadName = file.getOriginalFileName();
            if (!StringUtils.hasText(downloadName)) {
                downloadName = file.getStoredFileName();
            }
            if (!StringUtils.hasText(downloadName)) {
                downloadName = "file.txt";
            }

            String encodedName = java.net.URLEncoder.encode(downloadName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(contentBytes.length)
                    .body(contentBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

