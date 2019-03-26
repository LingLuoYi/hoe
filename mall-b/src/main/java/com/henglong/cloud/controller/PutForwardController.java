package com.henglong.cloud.controller;

import com.henglong.cloud.service.PutForwardService;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

//@CrossOrigin(allowCredentials="true")
//@RestController
//@RequestMapping("/put")
//方法废弃
public class PutForwardController {

    private final PutForwardService putForwardService;

    @Autowired
    public PutForwardController(PutForwardService putForwardService) {
        this.putForwardService = putForwardService;
    }

    @RequiresPermissions(value = {"admin:install","user:install"})
//    @PostMapping("/submission")
    public Json Put(String id,@RequestParam("num") String num){
        return putForwardService.Put(id,BigDecimal.valueOf(Long.valueOf(num)));
    }

    @RequiresPermissions(value = {"admin:update","finance:update"})
//    @PostMapping("/submission_admin")
    public Json PutS(String id,String hash,String b){
        return putForwardService.PutS(id,hash,Boolean.getBoolean(b));
    }
}
