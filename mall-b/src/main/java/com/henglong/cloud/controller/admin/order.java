package com.henglong.cloud.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/order")
public class order {

    @RequestMapping("/")
    public String allOrder(){
        return "order";
    }

    @RequestMapping("/wallet")
    public String allWallet(){
        return "wallet";
    }

    @RequestMapping("/update")
    public String updateWallet(){
        return "layer/updateOrder";
    }
}
