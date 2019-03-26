package com.henglong.cloud.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/login")
public class login {

    @RequestMapping("/")
    public String adminLogin(){
        return "login";
    }
}
