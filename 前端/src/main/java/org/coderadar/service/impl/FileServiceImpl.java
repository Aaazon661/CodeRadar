package org.coderadar.service.impl;

import lombok.RequiredArgsConstructor;
import org.coderadar.dao.FileDAO;
import org.coderadar.pojo.UserFile;
import org.coderadar.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileDAO fileDAO;

    @Override
    public UserFile upload(UserFile file) {
        fileDAO.insert(file);
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
    public int delete(Long fileId) {
        return fileDAO.softDelete(fileId);
    }

    @Override
    public int update(UserFile file) {
        return fileDAO.update(file);
    }
}
