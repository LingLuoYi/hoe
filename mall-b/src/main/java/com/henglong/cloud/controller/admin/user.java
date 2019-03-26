package com.henglong.cloud.controller.admin;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/user")
public class user {


    @RequiresPermissions(value = {"admin:select"},logical = Logical.OR)
    @RequestMapping("/all")
    public String userAll(){
        return "userAll";
    }

    @RequestMapping("/add")
    public String userAdd(){
        return "addUser";
    }

    @RequestMapping("/examine")
    public String examineUser(){
        return "userExamine";
    }

    @RequestMapping("/update")
    public String updateUser(){
        return "layer/updateUser";
    }

    @RequestMapping("/role")
    public String roles(){
        return "layer/updateRole";
    }

    @RequestMapping("/layer/examine")
    public String userExamine(){
        return "layer/examineUser";
    }

    @RequestMapping("/admin/code")
    public String adminpassword(){
        return "adminpassword";
    }
}
