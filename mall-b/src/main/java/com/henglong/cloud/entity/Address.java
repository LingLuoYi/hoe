package com.henglong.cloud.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity(name = "user_address")
public class Address {

    @Id
    @GeneratedValue(generator="_native")
    @GenericGenerator(name="_native", strategy="native")
    private Integer id;

    @Column(length = 50)
    @NotBlank(message = "收货人不能为空")
    private String name;

    @Column(length = 50)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Column(length = 50)
    private String email;

    @Column(length = 50)
    @NotBlank(message = "收货地址不能为空")
    private String address;

    @Column(length = 50)
    private String label;

    @Column(length = 1)
    private Integer defaults;

//    @ManyToOne
//    @JoinColumn(name="address_id")
//    private User user;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getDefaults() {
        return defaults;
    }

    public void setDefaults(Integer defaults) {
        this.defaults = defaults;
    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
}
