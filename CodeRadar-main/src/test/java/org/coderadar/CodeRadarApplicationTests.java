package org.coderadar;

import org.coderadar.dao.SuggestionDAO;
import org.coderadar.dao.UserDAO;
import org.coderadar.pojo.Suggestion;
import org.coderadar.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

@SpringBootTest
class CodeRadarApplicationTests {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private FileService fileService;
    @Autowired
    private SuggestionDAO suggestionDAO;

    @Test
    void contextLoads() {
        fileService.save("ceshiceshiceshi.txt","与有时即使学");
    }

    @Test
    void testFileType(){
        String original = "py测试.py";
        String fileType = "";
        if (StringUtils.hasText(original) && original.contains(".")) {
            fileType = original.substring(original.lastIndexOf('.') + 1);
        }
        System.out.println(fileType);
    }

    @Test
    void testLineStart(){
        Suggestion sug = suggestionDAO.findBySuggestionId(232L);
        Integer sugLine = sug.getLineStart();
        System.out.println(sugLine);
    }

}
