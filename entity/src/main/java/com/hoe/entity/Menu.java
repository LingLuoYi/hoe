package com.hoe.entity;

import javax.persistence.*;

@Entity(name = "Cloud_menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name ="menu_title", length = 50)
    private String title;
    //标题

    @Column(name = "menu_attribute", length = 50)
    private String attribute;
    //外链

    @Column(name =  "menu_describe", length = 50)
    private String describe;
    //描述

    @Column(name = "menu_enable")
    private Integer enable;
    //启用

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }
}
