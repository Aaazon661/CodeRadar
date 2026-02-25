package org.coderadar.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerImpl {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}
