package com.henglong.cloud.controller;

import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.MessageUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(allowCredentials="true")
@RestController
public class LogoutController {


    @RequestMapping("/logout")
    public Json Logout(){
        //使用权限管理工具进行用户的退出，跳出登录，给出提示信息
        SecurityUtils.getSubject().logout();
        return API.Success(MessageUtils.get("logout.ok"));
    }
}
