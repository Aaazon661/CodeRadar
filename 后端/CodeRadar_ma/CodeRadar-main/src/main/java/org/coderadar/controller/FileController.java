package org.coderadar.controller;

import org.coderadar.common.ResultResponse;
import org.coderadar.pojo.UserFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileController {
    ResultResponse<Object> upload(@RequestParam("userId") Long userId, @RequestParam("file") MultipartFile file);
    ResultResponse<UserFile> getById(@PathVariable Long fileId);
    ResultResponse<List<UserFile>> listByUser(@RequestParam("userId") Long userId);
    ResultResponse<Void> delete(@PathVariable Long fileId);
}
