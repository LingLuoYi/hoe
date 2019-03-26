package com.henglong.cloud.controller;

import com.henglong.cloud.entity.Menu;
import com.henglong.cloud.service.MenuServer;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/menu")
public class MenuController{

    private final MenuServer menuServer;

    @Autowired
    public MenuController(MenuServer menuServer) {
        this.menuServer = menuServer;
    }

    @RequestMapping("/menu_title")
    public Json MenuTitle(String title){
        return menuServer.MenuTitle(title);
    }

    @RequestMapping("/menu_all")
    public Json MenuAll(){
        return menuServer.MenuAll();
    }

    @RequiresPermissions(value = {"admin:update"})
    @RequestMapping("/menu_update")
    public Json MenuUpdate(Menu menu){
        return menuServer.MenuUpdate(menu);
    }

    @RequiresPermissions(value = {"admin:install"})
    @RequestMapping("/menu_add")
    public Json MenuAdd(Menu menu){
        return menuServer.MenuAdd(menu);
    }

    @RequiresPermissions(value = "admin:install")
    @RequestMapping("/menu_enable")
    public Json MenuEnable(@RequestParam("id") Integer id,@RequestParam("enable") Integer enable){
        return menuServer.MenuEnable(id,enable);
    }
}
