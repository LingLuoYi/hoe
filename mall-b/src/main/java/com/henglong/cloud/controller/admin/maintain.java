package com.henglong.cloud.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/maintain")
public class maintain {

    @RequestMapping("/")
    public String allMaintain(){
        return "maintain";
    }

    @RequestMapping("/update")
    public String updateMaintain(){
        return "layer/updateMaintain";
    }
}
