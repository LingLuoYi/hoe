package com.henglong.cloud.service;

import com.henglong.cloud.dao.MenuDao;
import com.henglong.cloud.entity.Menu;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.MessageUtils;
import com.henglong.cloud.util.Regular;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuServer {

    private final MenuDao menuDao;

    @Autowired
    public MenuServer(MenuDao menuDao){
        this.menuDao = menuDao;
    }

    //根据标题查询
    public Json MenuTitle(String title){

        Menu menu = menuDao.findByTitle(title);
        return API.Success(menu);
    }

    //查询全部菜单
    public Json MenuAll(){
        return API.Success(menuDao.findAll());
    }

    //修改菜单
    public Json MenuUpdate(Menu menu){
        if (!Regular.isEntity(menu))
            return API.error("参数错误");
        Optional<Menu> menus = menuDao.findById(menu.getId());
        if (menus.isPresent()){
            Menu menu1 = menus.get();
            if (menu.getTitle() != null && !"".equals(menu.getTitle()))
                menu1.setTitle(menu.getTitle());
            if (menu.getAttribute() != null && !"".equals(menu.getAttribute()))
                menu1.setAttribute(menu.getAttribute());
            if (menu.getDescribe() !=null && !"".equals(menu.getDescribe()))
                menu1.setDescribe(menu.getDescribe());
            if (menu.getEnable() != null)
                menu1.setEnable(menu.getEnable());
            menuDao.save(menu1);
            return API.Success(menu1);
        }else {
            return API.error(MessageUtils.get("menu.expire"));
        }
    }

    //添加菜单
    public Json MenuAdd(Menu menu){
        if (!Regular.isEntity(menu))
            return API.error("参数错误");
        if (menu.getTitle() == null || "".equals(menu.getTitle()))
            return API.error(MessageUtils.get("menu.title.expire"));
        menuDao.save(menu);
        return API.Success(menu);
    }

    //启停菜单
    public Json MenuEnable(Integer id ,Integer enable){
        Optional<Menu> menus = menuDao.findById(id);
        if (menus.isPresent()){
            Menu menu = menus.get();
            if(enable == 0)
                menu.setEnable(enable);
            else if(enable == 1)
                menu.setEnable(enable);
            else
                return API.error(MessageUtils.get("menu.stop"));
            menuDao.save(menu);
            return API.Success(MessageUtils.get("currency.success"));
        }else {
            return API.error(MessageUtils.get("menu.expire"));
        }
    }

}
