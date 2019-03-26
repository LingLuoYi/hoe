package com.henglong.cloud.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@Entity(name = "cloud_spread")
public class Spread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "spread_user_id")
    @NotBlank(message = "UserId不能为空")
    private String userId;

    @Column(name = "spread_phone")
    private String spreadPhone;

    @Column(name = "spread_email")
    private String spreadEmail;

    //自己的推荐码
    @Column(name = "spread_promo_code")
    private String spreadPromoCode;

    @Column(name =  "spread_url")
    private String spreadUrl;

    @Column(name = "spread_num")
    private Integer spreadNum;

    @Column(name = "spread_money")
    private Double spreadMoney;

    //上级的推荐码
    @Column(name = "spread_code")
    private String spreadCode;

    @Transient
    private Map<String,Double> profit;

    @Transient
    private Spread[] spreadUser;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSpreadPhone() {
        return spreadPhone;
    }

    public void setSpreadPhone(String spreadPhone) {
        this.spreadPhone = spreadPhone;
    }

    public String getSpreadEmail() {
        return spreadEmail;
    }

    public void setSpreadEmail(String spreadEmail) {
        this.spreadEmail = spreadEmail;
    }

    public String getSpreadPromoCode() {
        return spreadPromoCode;
    }

    public void setSpreadPromoCode(String spreadPromoCode) {
        this.spreadPromoCode = spreadPromoCode;
    }

    public String getSpreadUrl() {
        return spreadUrl;
    }

    public void setSpreadUrl(String spreadUrl) {
        this.spreadUrl = spreadUrl;
    }

    public Integer getSpreadNum() {
        return spreadNum;
    }

    public void setSpreadNum(Integer spreadNum) {
        this.spreadNum = spreadNum;
    }

    public Double getSpreadMoney() {
        return spreadMoney;
    }

    public void setSpreadMoney(Double spreadMoney) {
        this.spreadMoney = spreadMoney;
    }

    public String getSpreadCode() {
        return spreadCode;
    }

    public void setSpreadCode(String spreadCode) {
        this.spreadCode = spreadCode;
    }

    public Map<String, Double> getProfit() {
        return profit;
    }

    public void setProfit(Map<String, Double> profit) {
        this.profit = profit;
    }

    public Spread[] getSpreadUser() {
        return spreadUser;
    }

    public void setSpreadUser(Spread[] spreadUser) {
        this.spreadUser = spreadUser;
    }

}

