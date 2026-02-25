package org.coderadar;

import org.coderadar.dao.UserDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CodeRadarApplicationTests {

    @Autowired
    private UserDAO userDAO;

    @Test
    void contextLoads() {
    }


}
