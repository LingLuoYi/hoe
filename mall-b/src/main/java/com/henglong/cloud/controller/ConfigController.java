package com.henglong.cloud.controller;

import com.henglong.cloud.entity.Config;
import com.henglong.cloud.service.ConfigServer;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(allowCredentials="true")
@RequestMapping("/config")
public class ConfigController {

    private final ConfigServer configServer;

    @Autowired
    public ConfigController(ConfigServer configServer) {
        this.configServer = configServer;
    }

    @RequiresPermissions("admin:select")
    @RequestMapping("/config_select")
    public Json configSelect(){
        return configServer.selectConfig();
    }

    @RequiresPermissions("admin:update")
    @PostMapping("/config_update")
    public Json configUpdate(Config config){
        return configServer.updateConfig(config);
    }
}
