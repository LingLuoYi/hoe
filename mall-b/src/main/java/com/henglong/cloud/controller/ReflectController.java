package com.henglong.cloud.controller;

import com.henglong.cloud.entity.Reflect;
import com.henglong.cloud.service.ReflectService;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/reflect")
@CrossOrigin(allowCredentials = "true")
public class ReflectController {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(ReflectController.class);

    private final ReflectService reflectService;

    @Autowired
    public ReflectController(ReflectService reflectService) {
        this.reflectService = reflectService;
    }


    @PostMapping("/reflect_add")
    public Json Reflect(@RequestParam("assetsId") String assetsId, @RequestParam("num") String num, String s) throws UnsupportedEncodingException {
        return reflectService.Reflects(assetsId,new BigDecimal(num),s);
    }

    @RequestMapping("/reflect_all")
    public Json ReflectAll(){
        return reflectService.reflectAll();
    }

    @RequestMapping("/reflect_admin_all")
    public Json adminReflect(Integer page, Integer limit,String state, Integer id){
        return reflectService.adminReflect(page,limit,state,id);
    }

    @RequestMapping("/reflect_page")
    public Json ReflectPage(Integer index,Integer size, String state){
        return reflectService.reflectPage(index,size,state);
    }

    @PostMapping("/reflect_examine")
    @RequiresPermissions(value = {"finance:auditing","admin:auditing"},logical = Logical.OR)
    public Json reflectExamine(@RequestParam("id") Integer id,@RequestParam("o") Integer o,String remarks) throws UnsupportedEncodingException {
        return reflectService.reflectExamine(id,o,remarks);
    }

    @PostMapping("/reflect_pay_coin")
    @RequiresPermissions(value = {"cashier:pay","admin:pay"},logical = Logical.OR)
    public Json reflectPayCoin(@RequestParam("id") Integer id, String num, String brokerage,String hash) throws UnsupportedEncodingException {
        return reflectService.reflectPayCoin(id, new BigDecimal(num), new BigDecimal(brokerage), hash);
    }

    @PostMapping("/reflect_admin_update")
    @RequiresPermissions(value = {"admin:update"},logical = Logical.OR)
    public Json reflectUpdate(Reflect reflect){
        return reflectService.updateReflect(reflect);
    }

}
