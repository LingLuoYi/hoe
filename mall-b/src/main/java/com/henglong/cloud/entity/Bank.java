package com.henglong.cloud.entity;

import javax.persistence.*;

@Entity(name = "cloud_bank")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /*网关，一般为api地址，转账方式为开户银行*/
    @Column(name = "gateway",length = 50)
    private String gateway;

    /*如名字，转账方式为收款人姓名*/
    @Column(name = "app_id",length = 50)
    private String appID;

    /*支付宝如民，微信支付为商户号，转账为银行卡账号*/
    @Column(name = "app_private_key",columnDefinition = "text")
    private String appPrivateKey;

    /*备份字段，支付宝为公钥，微信为商户应用密钥*/
    @Column(name = "spare",columnDefinition = "text")
    private String spare;

    /*付款币种，转账方式使用，其他方式忽略*/
    @Column(name = "pay_type",length = 10)
    private String payType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getAppPrivateKey() {
        return appPrivateKey;
    }

    public void setAppPrivateKey(String appPrivateKey) {
        this.appPrivateKey = appPrivateKey;
    }

    public void setSpare(String spare) {
        this.spare = spare;
    }

    public String getSpare() {
        return spare;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayType() {
        return payType;
    }
}
