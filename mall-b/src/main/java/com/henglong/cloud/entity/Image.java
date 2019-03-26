package com.henglong.cloud.entity;

import javax.persistence.*;

@Entity(name = "Cloud_company_image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//图片id

    @Column(name = "img_name",length = 50)
    private String name;//图片名称

    @Column(name = "image_url")
    private String imageUrl;//图片连接

    @Column(name = "image_spare_url")
    private String spareUrl;//图片备用连接

    @Column(name = "image_attribute")
    private String attribute;//图片属性

    @Column(name = "image_describe",length = 50)
    private String describe;//图片描述

    @Column(name = "image_other")
    private String other;//备用字段

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSpareUrl() {
        return spareUrl;
    }

    public void setSpareUrl(String spareUrl) {
        this.spareUrl = spareUrl;
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

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
