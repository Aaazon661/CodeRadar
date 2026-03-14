package org.coderadar.service;

import org.coderadar.pojo.UserFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileService {

    UserFile upload(UserFile file);

    UserFile getById(Long fileId);

    List<UserFile> getUserFiles(Long userId);

    int delete(Long fileId);

    int update(UserFile file);

    String readFileContent(String filePath) throws IOException;

    int save(String UPLOAD_DIR,String stored, MultipartFile file);

    int save(String stored, String content);
}
