package com.henglong.cloud.controller.admin;

import com.henglong.cloud.dao.UserDao;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/index")
public class index {

    private final UserDao userDao;

    @Autowired
    public index(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping("/")
    public String adminIndex(Model model){
        model.addAttribute("user",userDao.findByUserId((String) SecurityUtils.getSubject().getPrincipal()));
        return "index";
    }
}
