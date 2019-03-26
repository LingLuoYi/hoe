package com.henglong.cloud.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name ="cloud_config")
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String desPass;
    //des加密密码，必须是8的倍数

    @Column(length = 11)
    private Integer saltLength;
    //用户加密密码长度

    @Column(columnDefinition = "decimal(19,16)")
    private BigDecimal coin;
    //理论出币

    private Double btcExchange;
    //btc价格

    private String adminEmail;
    //接收通知的邮箱

    private String coding;
    //编码

    private String amdinIp;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesPass() {
        return desPass;
    }

    public void setDesPass(String desPass) {
        this.desPass = desPass;
    }


    public Integer getSaltLength() {
        return saltLength;
    }

    public void setSaltLength(Integer saltLength) {
        this.saltLength = saltLength;
    }

    public BigDecimal getCoin() {
        return coin;
    }

    public void setCoin(BigDecimal coin) {
        this.coin = coin;
    }

    public Double getBtcExchange() {
        return btcExchange;
    }

    public void setBtcExchange(Double btcExchange) {
        this.btcExchange = btcExchange;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getCoding() {
        return coding;
    }

    public void setCoding(String coding) {
        this.coding = coding;
    }

    public String getAmdinIp() {
        return amdinIp;
    }

    public void setAmdinIp(String amdinIp) {
        this.amdinIp = amdinIp;
    }
}
