package org.coderadar.service;

import org.coderadar.pojo.UserFile;

import java.util.List;

public interface FileService {
    UserFile upload(UserFile file);

    UserFile getById(Long fileId);

    List<UserFile> getUserFiles(Long userId);

    int delete(Long fileId);

    int update(UserFile file);

}
