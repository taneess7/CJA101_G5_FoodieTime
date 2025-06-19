package com.foodietime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/index")
    public String homepage() {
        return "index";
    }

    @GetMapping("login")
    public String login() {
        return "front/member/login";
    }
}
