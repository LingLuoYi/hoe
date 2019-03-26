package com.henglong.cloud.controller;

import com.alipay.api.AlipayApiException;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.service.AssetsService;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/assets")
public class AssetsController {

    private final AssetsService assetsService;

    @Autowired
    public AssetsController(AssetsService assetsService) {
        this.assetsService = assetsService;
    }


    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/assets_info")
    public Json AssetsOneAllInfo (){
        return assetsService.AssetsOneAllInfo();
    }

    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/assets_page")
    public Json AssetsPage(Integer index,Integer size,String state){
        return assetsService.AssetsPage(index,size,state);
    }

    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/assets_id")
    public Json AssetsForId(@RequestParam("id") String id ,Integer index ,Integer size){
        return assetsService.AssetsForById(id,index,size);
    }

    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/assets_total")
    public Json AssetsAllTotal(){
        return assetsService.AssetsAllTotal();
    }

    @RequestMapping("/assets_profit_total")
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    public Json AssetsProfitTotal(){
        return assetsService.AssetsProfitTotal();
    }

    @RequestMapping("/assets_frozen_profit")
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    public Json AssetsFrozenProfit(){
        return assetsService.AssetsFrozenProfitTotal();
    }

    @RequestMapping("/assets_three")
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    public Json Three(){
        return assetsService.Three();
    }


    /**
     * 微信支付回调
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/sdfjsaksjadghushfnxcsdfksdjafhusahdf/sfsdfasifhx/qwexfdse")
    public String WeChatExamine (HttpServletRequest request, HttpServletResponse response) throws Exception {
        return assetsService.WeChatExamine(request,response);
    }

    /**
     * 支付宝回调
     * @param request
     * @return
     * @throws ParseException
     * @throws AlipayApiException
     */
    @RequestMapping("/sdsdfsdfsdasdfasfsadfsadfsdfwewrwrtrqu/werterrweqcxvx/sewrsdfw")
    public String AlipayExamine(HttpServletRequest request) throws ParseException, AlipayApiException {
        return assetsService.AlipayExamine(request);
    }

    /**
     * 审核接口，传入审核订单id
     * @param id
     * @return
     */
    @RequiresPermissions("finance:auditing")
    @PostMapping("/examine_finance")
    public Json Examine(@RequestParam("id") String id,@RequestParam("o") String o,String payReceipts,String payTypeId,String s) throws UnsupportedEncodingException {
        return assetsService.Examine(id,o,payReceipts,payTypeId,s);
    }

    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/assets_admin_all")
    public Json AssetsAll(Integer page,Integer limit,String id){
        return assetsService.AssetsAll(page,limit,id);
    }

    @RequiresPermissions(value = {"finance:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/assets_admin_one")
    public Json AssetsOne(@RequestParam("id") Integer id){
        return assetsService.AssetsOne(id);
    }

    @RequiresPermissions(value = {"admin:update"},logical = Logical.OR)
    @RequestMapping("/assets_admin_update")
    public Json AssetsUpdate(@Valid Assets assets){
        return assetsService.AssetsUpdate(assets);
    }

    @RequestMapping("/assets_update_date_day")
    @RequiresPermissions(value = {"admin:update"},logical = Logical.OR)
    public Json sdf(@RequestParam("userId") String userId) throws ParseException {
        return assetsService.sdfs(userId);
    }
}
