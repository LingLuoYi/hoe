package com.henglong.cloud.controller;

import com.henglong.cloud.service.SpreadService;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/spread")
public class SpreadController {

    private final SpreadService spreadService;

    @Autowired
    public SpreadController(SpreadService spreadService) {
        this.spreadService = spreadService;
    }

    @RequiresPermissions("admin:select")
    @RequestMapping("/spread_admin_all_info")
    public Json SpreadAllInfo(){
        return spreadService.SpreadAllInfo();
    }

    @RequiresPermissions("user:select")
    @RequestMapping("/spread_one_info")
    public Json SpreadOneInfo(){
        return spreadService.SpreadOneInfo();
    }
}
