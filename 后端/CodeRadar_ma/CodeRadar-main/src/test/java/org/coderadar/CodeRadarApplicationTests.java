package org.coderadar;

import org.coderadar.dao.UserDAO;
import org.coderadar.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CodeRadarApplicationTests {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private FileService fileService;

    @Test
    void contextLoads() {
        fileService.save("ceshiceshiceshi.txt","与有时即使学");
    }


}
