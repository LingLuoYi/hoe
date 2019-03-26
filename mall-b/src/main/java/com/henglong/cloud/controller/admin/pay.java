package com.henglong.cloud.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/pay")
public class pay {

    @RequestMapping("/")
    public String allPay(){
        return "pay";
    }

    @RequestMapping("/examine")
    public String examinePay(){
        return "payExamine";
    }

    @RequestMapping("/update")
    public String updatePay(){
        return "layer/updatePay";
    }

    @RequestMapping("/layer/examine")
    public String payExamine(){
        return "layer/examinePay";
    }

    @RequestMapping("/bank")
    public String bank(){
        return "bank";
    }

    @RequestMapping("/layer/bank")
    public String sBank(){
        return "layer/bank";
    }
}
