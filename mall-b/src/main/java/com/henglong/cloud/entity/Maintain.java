package com.henglong.cloud.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 维护费用单
 */
@Entity(name = "Cloud_maintain")
public class Maintain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "maintain_id",length = 50)
    private String maintainId;

    @Column(name = "user_id",length = 50)
    private String userId;

    @Column(name = "commodity_id",length = 100)
    private String commodityId;

    @Column(name = "assetsId",length = 100)
    private String assetsId;

    @Column(name = "money")
    private Double money;
    //总金额

    @Column(name = "term",length = 20)
    private Integer term;
    //缴纳天数

    @Column(name = "time")
    private Date time;
    //下单时间

    @Column(name = "pay_type",length = 20)
    private String payType;

    @Column(name = "state",length = 10)
    private Integer state;
    //状态

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaintainId() {
        return maintainId;
    }

    public void setMaintainId(String maintainId) {
        this.maintainId = maintainId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(String assetsId) {
        this.assetsId = assetsId;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
