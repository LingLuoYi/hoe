package com.henglong.cloud.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/reflect")
public class reflect {

    @RequestMapping("/examine")
    public String reflectExamine(){
        return "reflectExamine";
    }

    @RequestMapping("/")
    public String allReflect(){
        return "reflect";
    }

    @RequestMapping("/update")
    public String updateReflect(){
        return "layer/updateReflect";
    }

    @RequestMapping("/layer/examine")
    public String examineReflect(){
        return "layer/examineReflect";
    }

    @RequestMapping("/coin")
    public String coin(){
        return "coin";
    }

    @RequestMapping("/payCoin")
    public String payCoin(){
        return "layer/payCoin";
    }
}
