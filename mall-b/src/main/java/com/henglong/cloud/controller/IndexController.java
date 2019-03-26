package com.henglong.cloud.controller;

import com.henglong.cloud.service.UserService;
import com.henglong.cloud.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@CrossOrigin(allowCredentials="true")
@RestController
public class IndexController {

    private static final Logger log = LogManager.getLogger(IndexController.class);

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/s")
    public Json GetIndex(){
        return userService.UserInfo();
    }

    @RequestMapping("/error/403")
    public Json unauthorizedRole(){
        log.info("------没有权限-------");
        return API.error(CodeConstant.NO_AUTH,MessageUtils.get("index.power"));
    }

    @RequestMapping("/")
    public Json ss(){
        return API.Success(MessageUtils.get("user.welcome"));
    }

    @RequestMapping("/admin_phone_code")
    public Json sdfsdfsa(String password, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        return userService.sdfsdfsa(password,request,response);
    }

    @PostMapping("/admin_ip_update")
    public Json sdfsfadfa(String code,HttpServletRequest request){
        return userService.sdfsda(IPUtils.getRealIP(request),code);
    }

    @RequestMapping("/pool_info")
    public String pool(){
        return HttpUtil.doGet("https://pool.api.btc.com/v1/pool/status/");
    }


}
