package com.henglong.cloud.controller;

import com.henglong.cloud.entity.Maintain;
import com.henglong.cloud.service.MaintainServer;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/maintain")
@CrossOrigin(allowCredentials="true")
public class MaintainController {

    private final MaintainServer maintainServer;

    @Autowired
    public MaintainController(MaintainServer maintainServer) {
        this.maintainServer = maintainServer;
    }

    @PostMapping("/maintain_pay")
    public Json collection(@RequestParam("assetsId") String assetsId,@RequestParam("num") Integer num){
        return maintainServer.collection(assetsId,num);
    }


    @RequestMapping("/maintain_page")
    public Json maintainPage(Integer index, Integer size, Integer state){
        return maintainServer.maintainPage(index,size,state);
    }

    @RequiresPermissions(value = {"admin:select","finance:select"},logical = Logical.OR)
    @RequestMapping("/maintain_admin_all")
    public Json adminMaintain(Integer page, Integer limit,String id, Integer state){
        return maintainServer.adminMaintain(page,limit,id,state);
    }

    @PostMapping("/maintain_admin_update")
    @RequiresPermissions(value = {"admin:update","finance:update"},logical = Logical.OR)
    public Json updateMaintain(Maintain maintain){
        return maintainServer.updateMaintain(maintain);
    }
}
