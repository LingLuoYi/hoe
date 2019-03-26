package com.henglong.cloud.controller;

import com.henglong.cloud.entity.Order;
import com.henglong.cloud.service.OrderService;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //单个用户订单信息
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/order_info")
    public Json OrderInfo(){
        return orderService.OrderInfo();
    }

    @RequestMapping("/order_info_page")
    public Json OrderPage(Integer index,Integer size,Integer state,String orderCommodityType,Integer maintainPayType){
        return orderService.OrderInfoPage(index,size,state,orderCommodityType,maintainPayType);
    }

    //用户修改自己的订单
    @RequiresPermissions(value = {"user:update","admin:update"},logical = Logical.OR)
    @PostMapping("/order_user_update")
    public Json OrderUserUpdate(@RequestParam("id") String id,@RequestParam("num") String num) throws Exception {
        return  orderService.orderUserUpdate(id,num);
    }

    //用户删除自己的订单
    @RequiresPermissions(value = {"user:delete","admin:delete"},logical = Logical.OR)
    @PostMapping("/order_user_delete")
    public Json OrderUserDelete(@RequestParam("id") String id){
        return orderService.orderUserDelete(id);
    }

    @RequiresPermissions(value = {"user:delete","admin:delete"},logical = Logical.OR)
    @RequestMapping("/order_user_close")
    public Json OrderUserClose(@RequestParam("id") String id,String s) throws Exception {
        return orderService.OrderClose(id,s);
    }

    //单个订单
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/order_one_info")
    public Json OrderOneInfo(@RequestParam("id") String id){
        return orderService.OrderOneInfo(id);
    }

    //全部订单信息-管理员获取
    @RequiresPermissions(value = {"finance:select","cashier:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/order_all_info")
    public Json OrderAllInfo(Integer page,Integer limit,String id,String state,String type){
        return orderService.OrderAllInfo(page,limit,id,state,type);
    }

    //单个订单(管理员)
    @RequiresPermissions(value = {"finance:select","cashier:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/order_admin_one_info")
    public Json OrderAdminOneInfo(@RequestParam("id") String id){
        return orderService.OrderAdminOneInfo(id);
    }

    //管理员删除订单
    @RequiresPermissions(value = {"finance:delete","cashier:delete","admin:delete"},logical = Logical.OR)
    @PostMapping("/order_admin_delete")
    public Json OrderDelete(@RequestParam("id") String id){
        return orderService.orderDelete(id);
    }


    @RequiresPermissions(value = {"finance:install","cashier:install","admin:install"},logical = Logical.OR)
    @PostMapping("/order_admin_update")
    public Json OrderUpdate(@Valid Order o){
        return orderService.orderUpdate(o);
    }


}
