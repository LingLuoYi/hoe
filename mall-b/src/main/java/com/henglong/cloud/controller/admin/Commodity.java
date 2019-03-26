package com.henglong.cloud.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/commodity")
public class Commodity {

    @RequestMapping("/add")
    public String addCommodity(){
        return "addCommodity";
    }

    @RequestMapping("/")
    public String allCommodity(){
        return "commodity";
    }

    @RequestMapping("/update")
    public String update(){
        return "layer/updateCommodity";
    }
}
