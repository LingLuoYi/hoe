package com.henglong.cloud.controller;

import com.henglong.cloud.entity.Bank;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.service.FileService;
import com.henglong.cloud.service.PayService;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.LoadFile;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/pay")
public class PayController {

    private static final Logger log = LoggerFactory.getLogger(PayController.class);

    private final PayService payService;

    private final FileService fileService;

    @Autowired
    public PayController(PayService payService, FileService fileService) {
        this.payService = payService;
        this.fileService = fileService;
    }

    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @PostMapping("/pay")
    public Object PayFirst(@RequestParam("id") String id, @RequestParam("type") String type, HttpServletRequest request) throws Exception {
        if (id.startsWith("CY")) {
            return payService.PayFirst(id,type,request);
        }else if (id.startsWith("MN")){
            return payService.MaintainPay(id,type,request.getRemoteAddr());
        }else {
            return API.error("请正确输入id");
        }

    }

    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @PostMapping("/maintain_pay")
    public Object maintainPay(@RequestParam("id") String id, @RequestParam("type") String type, HttpServletRequest request) throws Exception {
        log.info("成功接收到数据【"+id+"】");
        return payService.MaintainPay(id,type,request.getRemoteAddr());
    }

    @RequiresPermissions(value = {"admin:select","finance:select"},logical = Logical.OR)
    @RequestMapping("/bank")
    public Json Bank(){
        return payService.BankAll();
    }

    @RequiresPermissions(value = {"admin:install","finance:install"},logical = Logical.OR)
    @RequestMapping("/add_bank")
    public Json AddBank(Bank bank){
        return payService.AddBank(bank);
    }

    /*上传文件*/
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/file_img")
    public Json File(@RequestParam(value = "pay_img",required = false) MultipartFile file ,@RequestParam("id") String id) throws Exception {
        return fileService.PayFile(file,id);
    }

    /*图片加载连接*/
    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_img/{name}")
    public void PayImg(HttpServletResponse response, @PathVariable("name")String name){
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(LoadFile.Path()+"/img/pay/"+name);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.warn("显示图片发生了异常",e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.warn("图片显示类，关闭流出现了异常",e);
                }
            }
        }
    }


    @RequiresPermissions(value = {"finance:update","admin:update"},logical = Logical.OR)
    @PostMapping("/bank_update")
    public Json BankUpdate(@Valid Bank bank){
        return payService.BankUpdate(bank);
    }

    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_voucher_state")
    public Json PayvoucherState(@RequestParam("sv") String sv){
        return payService.PayvoucherState(sv);
    }

    @RequiresPermissions(value = {"finance:select","cashier:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_state")
    public Json PayState(@RequestParam("s") String s){
        return payService.PayState(s);
    }

//    @RequiresPermissions(value = {"finance:select","cashier:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_one")
    public Json PayOne(@RequestParam("id") String id){
        return payService.PayOne(id);
    }

    @RequiresPermissions(value = {"finance:select","cashier:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_admin_all")
    public Json PayAll(Integer page, Integer limit, String id, String state,String voucherState){
        return payService.PayAll(page,limit,id,state,voucherState);
    }

    @RequiresPermissions(value = {"admin:delete"},logical = Logical.OR)
    @PostMapping("/pay_delete")
    public Json PayDelete(@RequestParam("id") String id){
        return payService.PayDelete(id);
    }

    @RequiresPermissions(value = {"finance:update","admin:update"},logical = Logical.OR)
    @PostMapping("/pay_update")
    public Json PayUpdate(@Valid Pay pay){
        return payService.PayUpdate(pay);
    }

    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/pay_user_info")
    public Json PayUser(){
        return payService.PayUser();
    }

}
