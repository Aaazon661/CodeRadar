package org.coderadar.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coderadar.dao.FileDAO;
import org.coderadar.dao.ResultDAO;
import org.coderadar.dao.SuggestionDAO;
import org.coderadar.pojo.Result;
import org.coderadar.pojo.Suggestion;
import org.coderadar.pojo.UserFile;
import org.coderadar.service.AImodelService;
import org.coderadar.service.FileService;
import org.coderadar.service.ReviewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final AImodelService aiModelService;
    private final FileDAO fileDAO;
    private final ResultDAO resultDAO;
    private final SuggestionDAO suggestionDAO;
    private final FileService fileService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String UPLOAD_DIR = "reviews";

    @Value("${ai.api.model}")
    private String apiModel;

    @Override
    @Transactional
    public Long submitReview(Long userId, Long fileId, String background, String model) {
        try {
            // 1. 根据文件ID获取文件
            UserFile file = fileDAO.findByFileId(fileId);
            if (file == null) {
                throw new IllegalArgumentException("文件不存在，fileId: " + fileId);
            }

            log.info("开始审查文件: fileId={}, fileName={}", fileId, file.getOriginalFileName());

            // 2. 构建完整的代码内容（包含背景说明）
            String codeContent = fileService.readFileContent(file.getStoragePath());
            String codeWithBackground = buildCodeWithBackground(codeContent, background);

            // 3. 调用AI进行分析
            String aiResponse = aiModelService.analyzeCode(codeWithBackground);
            log.debug("AI响应: {}", aiResponse);

            // 4. 解析AI响应为结构化数据
            ParsedReviewResult parsedResult = parseAiResponse(aiResponse);

            // 5. 保存审查结果（事务性）
            LocalDateTime now = LocalDateTime.now();
            Result result = Result.builder()
                    .requestId(parsedResult.getRequestId())
                    .userId(userId)
                    .fileId(fileId)
                    .model(apiModel)
                    .summary(parsedResult.getSummary())
                    .reviewTime(now)
                    .rawJson(aiResponse)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            resultDAO.insert(result);
            log.info("保存审查结果: resultId={}, requestId={}", result.getResultId(), result.getRequestId());

            // 6. 批量保存建议（事务性）
            if (parsedResult.getSuggestions() != null && !parsedResult.getSuggestions().isEmpty()) {
                List<Suggestion> suggestions = new ArrayList<>();
                for (SuggestionData sugData : parsedResult.getSuggestions()) {
                    Suggestion suggestion = Suggestion.builder()
                            .resultId(result.getResultId())
                            .requestId(result.getRequestId())
                            .category(sugData.getCategory())
                            .lineNumbers(sugData.getLineNumbers())
                            .suggestion(sugData.getSuggestion())
                            .severity(sugData.getSeverity())
                            .lineStart(parseLineStart(sugData.getLineNumbers()))
                            .status("PENDING")
                            .createdAt(now)
                            .build();
                    suggestions.add(suggestion);
                }

                // 批量插入建议
                if (suggestions.size() == 1) {
                    suggestionDAO.insert(suggestions.get(0));
                } else {
                    suggestionDAO.batchInsert(suggestions);
                }
                log.info("保存建议数量: {}", suggestions.size());
            }

            return result.getResultId();
        } catch (Exception e) {
            log.error("代码审查失败: userId={}, fileId={}", userId, fileId, e);
            throw new RuntimeException("代码审查失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Long generateReviewedFile(Long resultId) {
        // 兼容旧接口：不带选中列表时，默认使用全部建议
        return generateReviewedFile(resultId, null);
    }

    @Override
    @Transactional
    public Long generateReviewedFile(Long resultId, List<Long> selectedSuggestionIds) {
        try {
            // 1. 获取审查结果
            Result result = resultDAO.findById(resultId);
            if (result == null) {
                throw new IllegalArgumentException("审查结果不存在，resultId: " + resultId);
            }

            // 2. 获取原始文件
            UserFile originalFile = fileDAO.findByFileId(result.getFileId());
            if (originalFile == null) {
                throw new IllegalArgumentException("原始文件不存在，fileId: " + result.getFileId());
            }

            // 3. 获取建议列表（如果指定了选中列表，只保留用户选中的那些）
            List<Suggestion> suggestions = suggestionDAO.findByResultId(resultId);
            if (suggestions == null || suggestions.isEmpty()) {
                log.warn("没有找到建议，直接返回原文件副本");
            } else if (selectedSuggestionIds != null && !selectedSuggestionIds.isEmpty()) {
                java.util.Set<Long> idSet = new java.util.HashSet<>(selectedSuggestionIds);
                suggestions = suggestions.stream()
                        .filter(s -> s.getSuggestionId() != null && idSet.contains(s.getSuggestionId()))
                        .collect(java.util.stream.Collectors.toList());
                if (suggestions.isEmpty()) {
                    log.warn("根据选中列表过滤后没有可用建议，将只返回原文件内容");
                }
            }

            // 4. 生成带注释的新文件内容
            String reviewedContent = insertSuggestionsIntoFile(
                    fileService.readFileContent(originalFile.getStoragePath()),
                    suggestions,
                    originalFile.getFileType()
            );

            // 5. 先创建一个与原文件几乎一致的新文件记录（调用 FileService 统一处理属性）
            LocalDateTime now = LocalDateTime.now();
            UserFile draftFile = UserFile.builder()
                    .userId(result.getUserId())
                    .originalFileName(generateReviewedFileName(originalFile.getOriginalFileName()))
                    .storedFileName(generateReviewedFileName(originalFile.getStoredFileName()))
                    .storagePath(originalFile.getStoragePath() + "_reviewed")
                    //.fileContent(originalFile.getFileContent())
                    .fileType(originalFile.getFileType())
                    .fileHash(originalFile.getFileHash())
                    .encoding(originalFile.getEncoding())
                    .newline(originalFile.getNewline())
                    .deleted(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            //生成文件，修改存储目录
            draftFile.setStoragePath(Paths.get(UPLOAD_DIR, draftFile.getStoragePath()).toString());
            fileService.save(draftFile.getStoragePath(),"");
            // 使用 FileService 负责上传，自动补充 fileSize、时间等属性
            UserFile createdFile = fileService.upload(draftFile);

            // 6. 再通过 FileService 的 update 功能，把审查建议插入后的内容写回该新文件
            System.err.println("即将写入新文本：" + reviewedContent);
            fileService.save(createdFile.getStoragePath(),reviewedContent);
            fileService.update(createdFile);

            log.info("生成审查后文件: newFileId={}, originalFileId={}", createdFile.getFileId(), originalFile.getFileId());

            return createdFile.getFileId();
        } catch (Exception e) {
            log.error("生成审查文件失败: resultId={}", resultId, e);
            throw new RuntimeException("生成审查文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void confirmReviewedFile(Long newFileId) throws Exception {
        try {
            // 1. 验证文件存在（通过 FileService 统一入口）
            UserFile newFile = fileService.getById(newFileId);
            if (newFile == null) {
                throw new IllegalArgumentException("审查文件不存在，fileId: " + newFileId);
            }

            // 2. 确认操作：文件已经保存，只需要更新状态或记录日志
            log.info("用户确认审查文件: fileId={}, fileName={}", newFileId, newFile.getOriginalFileName());

            // 可选：更新文件的某些元数据，比如标记为"已确认"
            // 这里可以根据业务需求添加额外逻辑

        } catch (Exception e) {
            log.error("确认审查文件失败: newFileId={}", newFileId, e);
            throw new Exception("确认审查文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void discardReviewedFile(Long newFileId) throws Exception {
        try {
            // 1. 验证文件存在
            UserFile newFile = fileService.getById(newFileId);
            if (newFile == null) {
                throw new IllegalArgumentException("审查文件不存在，fileId: " + newFileId);
            }

            // 2. 软删除该文件（通过 FileService 封装）
            int deleted = fileService.delete(newFileId);
            if (deleted > 0) {
                log.info("用户放弃审查文件，已删除: fileId={}, fileName={}", newFileId, newFile.getOriginalFileName());
            } else {
                log.warn("删除审查文件失败: fileId={}", newFileId);
                throw new RuntimeException("删除审查文件失败");
            }

        } catch (Exception e) {
            log.error("放弃审查文件失败: newFileId={}", newFileId, e);
            throw new Exception("放弃审查文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Object getReviewDetail(Long resultId) {
        Result result = resultDAO.findById(resultId);
        if (result == null) {
            return null;
        }

        List<Suggestion> suggestions = suggestionDAO.findByResultId(resultId);

        return Map.of(
                "result", result,
                "suggestions", suggestions
        );
    }

    @Override
    public Object getFileReviewHistory(Long fileId) {
        List<Result> results = resultDAO.findByFileId(fileId);
        return results;
    }

    /**
     * 构建包含背景说明的代码内容
     */
    private String buildCodeWithBackground(String codeContent, String background) {
        if (background == null || background.trim().isEmpty()) {
            return codeContent;
        }
        return "背景说明：\n" + background + "\n\n代码内容：\n" + codeContent;
    }

    /**
     * 解析AI响应为结构化数据
     */
    private ParsedReviewResult parseAiResponse(String aiResponse) {
        try {
            JsonNode root = objectMapper.readTree(aiResponse);

            ParsedReviewResult result = new ParsedReviewResult();
            // 为避免与数据库中唯一约束冲突，不再使用 AI 返回的 requestId，统一由后端生成唯一 ID
            result.setRequestId(UUID.randomUUID().toString());
            result.setSummary(root.has("summary") ? root.get("summary").asText() : "AI审查完成");

            List<SuggestionData> suggestions = new ArrayList<>();
            if (root.has("suggestions") && root.get("suggestions").isArray()) {
                for (JsonNode sugNode : root.get("suggestions")) {
                    SuggestionData sug = new SuggestionData();
                    sug.setCategory(sugNode.has("category") ? sugNode.get("category").asText() : "其他");
                    sug.setLineNumbers(sugNode.has("lineNumbers") ? sugNode.get("lineNumbers").asText() : "");
                    sug.setSuggestion(sugNode.has("suggestion") ? sugNode.get("suggestion").asText() : "");
                    sug.setSeverity(sugNode.has("severity") ? sugNode.get("severity").asText() : "MEDIUM");
                    suggestions.add(sug);
                }
            }
            result.setSuggestions(suggestions);

            return result;
        } catch (Exception e) {
            log.error("解析AI响应失败，使用默认值", e);
            ParsedReviewResult fallback = new ParsedReviewResult();
            fallback.setRequestId(UUID.randomUUID().toString());
            fallback.setSummary("AI审查完成（解析失败）");
            fallback.setSuggestions(new ArrayList<>());
            return fallback;
        }
    }

    /**
     * 从行号字符串中解析起始行号
     */
    private Integer parseLineStart(String lineNumbers) {
        if (lineNumbers == null || lineNumbers.trim().isEmpty()) {
            return null;
        }
        try {
            // 支持格式: "10", "10-15", "10,11,12"
            String[] parts = lineNumbers.split("[-,]");
            return Integer.parseInt(parts[0].trim());
        } catch (Exception e) {
            log.warn("解析行号失败: {}", lineNumbers);
            return null;
        }
    }

    /**
     * 将建议插入到文件内容中
     */
    private String insertSuggestionsIntoFile(String originalContent, List<Suggestion> suggestions, String fileType) {
        if (suggestions == null || suggestions.isEmpty()) {
            return originalContent;
        }

        String[] lines = originalContent.split("\n");
        StringBuilder result = new StringBuilder();

        // 按行号排序建议
        suggestions.sort((a, b) -> {
            Integer lineA = a.getLineStart() != null ? a.getLineStart() : 0;
            Integer lineB = b.getLineStart() != null ? b.getLineStart() : 0;
            return lineA.compareTo(lineB);
        });

        int suggestionIndex = 0;
        for (int i = 0; i < lines.length; i++) {
            int currentLine = i + 1;

            // 插入该行之前的所有建议
            while (suggestionIndex < suggestions.size()) {
                Suggestion sug = suggestions.get(suggestionIndex);
                Integer sugLine = sug.getLineStart();

                if (sugLine != null && sugLine == currentLine) {
                    // 插入注释
                    String comment = formatSuggestionAsComment(sug, fileType);
                    result.append(comment).append("\n");
                    suggestionIndex++;
                } else {
                    break;
                }
            }

            // 插入原始行
            result.append(lines[i]).append("\n");
        }

        // 添加剩余的建议到文件末尾
        while (suggestionIndex < suggestions.size()) {
            Suggestion sug = suggestions.get(suggestionIndex);
            String comment = formatSuggestionAsComment(sug, fileType);
            result.append(comment).append("\n");
            suggestionIndex++;
        }

        return result.toString();
    }

    /**
     * 将建议格式化为注释
     * 说明：在注释中只展示简短错误类型（如「逻辑错误」「安全风险」），
     * 行号前加上「行」说明，不再在方括号里拼接 MEDIUM/HIGH 等级，避免冗长。
     */
    private String formatSuggestionAsComment(Suggestion sug, String fileType) {
        String commentPrefix = getCommentPrefix(fileType);
        String categoryLabel = toPrettyCategory(sug.getCategory());
        String lineInfo = (sug.getLineNumbers() != null && !sug.getLineNumbers().isEmpty())
                ? "行" + sug.getLineNumbers()   // 行和数字之间不留空格
                : "相关行";

        // 注释格式示例：// ("安全风险"):    行35-41: 具体建议
        return String.format("%s (\"%s\"):    %s: %s",
                commentPrefix,
                categoryLabel,
                lineInfo,
                sug.getSuggestion());
    }

    /**
     * 将原始的分类字段转换为更友好的中文标签
     */
    private String toPrettyCategory(String rawCategory) {
        if (rawCategory == null) {
            return "代码问题";
        }
        String c = rawCategory.trim();
        if (c.isEmpty()) {
            return "代码问题";
        }
        String lower = c.toLowerCase();

        // 常见英文分类映射
        if (lower.contains("security")) return "安全风险";
        if (lower.contains("performance")) return "性能问题";
        if (lower.contains("readability")) return "可读性问题";
        if (lower.contains("style")) return "代码风格";
        if (lower.contains("logic")) return "逻辑错误";
        if (lower.contains("bug")) return "潜在 Bug";

        // 常见中文关键词映射
        if (c.contains("安全")) return "安全风险";
        if (c.contains("性能")) return "性能问题";
        if (c.contains("可读")) return "可读性问题";
        if (c.contains("风格")) return "代码风格";
        if (c.contains("逻辑")) return "逻辑错误";
        if (c.contains("并发") || c.contains("线程")) return "并发问题";

        // 默认回退：直接使用原分类或通用标签
        return c;
    }

    /**
     * 根据文件类型获取注释前缀
     */
    private String getCommentPrefix(String fileType) {
        if (fileType == null) return "//";
        switch (fileType.toLowerCase()) {
            case "java":
            case "js":
            case "ts":
            case "cpp":
            case "c":
                return "//";
            case "py":
            case "sh":
                return "#";
            case "html":
            case "xml":
                return "<!--";
            default:
                return "//";
        }
    }

    /**
     * 生成审查后的文件名
     */
    private String generateReviewedFileName(String originalName) {
        if (originalName == null) {
            return "reviewed_" + System.currentTimeMillis();
        }
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            String name = originalName.substring(0, dotIndex);
            String ext = originalName.substring(dotIndex);
            return name + "_reviewed" + ext;
        }
        return originalName + "_reviewed";
    }

    /**
     * 内部类：解析后的审查结果
     */
    private static class ParsedReviewResult {
        private String requestId;
        private String summary;
        private List<SuggestionData> suggestions;

        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public List<SuggestionData> getSuggestions() { return suggestions; }
        public void setSuggestions(List<SuggestionData> suggestions) { this.suggestions = suggestions; }
    }

    /**
     * 内部类：建议数据
     */
    private static class SuggestionData {
        private String category;
        private String lineNumbers;
        private String suggestion;
        private String severity;

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getLineNumbers() { return lineNumbers; }
        public void setLineNumbers(String lineNumbers) { this.lineNumbers = lineNumbers; }
        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
    }
}
