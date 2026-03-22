package org.coderadar;

import org.coderadar.dao.FileDAO;
import org.coderadar.dao.ResultDAO;
import org.coderadar.dao.SuggestionDAO;
import org.coderadar.dao.UserDAO;
import org.coderadar.pojo.Result;
import org.coderadar.pojo.Suggestion;
import org.coderadar.pojo.User;
import org.coderadar.pojo.UserFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class CodeRadarApplicationTests {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private ResultDAO resultDAO;

    @Autowired
    private SuggestionDAO suggestionDAO;

    @Test
    void contextLoads() {
    }

//    @Test
//    void testUserDAO() {
//        userDAO.testAddUser();
//    }

    @Test
    void testUserDAO() {
        List<User> users = userDAO.findAll();
        for(User user : users) {
            System.out.println(user);
        }
    }

    @Test
    void testFileDAO() {
        UserFile file = new UserFile();
        file.setUserId(4L);
//        file.setOriginalFileName("test");
//        file.setStoredFileName("test");
//        file.setStoragePath("test");
//        file.setFileType("test");
//        fileDAO.insert(file);
        System.out.println(fileDAO.findByUserId(file.getUserId()));
    }

    @Test
    void testResultDAO() {
        Result result = new Result();
        result.setUserId(4L);
        result.setRequestId("test");
        result.setFileId(1L);
        result.setModel("DeepSeek");
//        resultDAO.insert(result);
        System.out.println(resultDAO.findByFileId(result.getFileId()));
    }

    @Test
    void testSuggestionDAO() {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        Suggestion suggestion1 = new Suggestion();
        Suggestion suggestion2 = new Suggestion();
        suggestion1.setResultId(1L);
        suggestion1.setCategory("SECURITY");
        suggestion1.setLineStart(20);
        suggestion1.setSuggestion("测试安全漏洞");
        suggestion1.setSeverity("LOW");

        suggestion2.setResultId(1L);
        suggestion2.setCategory("PERFORMANCE");
        suggestion2.setLineStart(20);
        suggestion2.setSuggestion("测试建议");
        suggestion2.setSeverity("HIGH");
        suggestions.add(suggestion1);
        suggestions.add(suggestion2);
        suggestionDAO.batchInsert(suggestions);

        System.out.println(suggestionDAO.findByResultId(suggestion1.getResultId()));
    }
}






















