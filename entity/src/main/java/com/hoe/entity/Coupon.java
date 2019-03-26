package com.hoe.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 优惠劵，总表
 */
@Entity(name = "Cloud_coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Integer couponId;

    //优惠劵名称
    @Column(name = "name",length = 100)
    private String name;

    //开始时间
    @Column(name = "start_time")
    private Date startTime;

    //结束时间
    @Column(name = "stop_time")
    private Date stopTime;

    //优惠劵类型，1 满减，目前只有满减定额劵
    @Column(name = "type",length = 1)
    private Integer type;

    //优惠劵满足金额,计算优惠劵的时候都可以换算成金额来实现规则验证
    @Column(name = "sun")
    private Double sun;

    //优惠劵面值，目前为固定金额
    @Column(name = "money")
    private Double money;

    //优惠劵适用的商品
    @OneToMany(targetEntity = Commodity.class)
    @JoinColumn(name = "id",columnDefinition = "coupon_id")
    private List<Commodity> commodities;

    //优惠劵的数量
    @Column(name = "num")
    private Integer num;

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getSun() {
        return sun;
    }

    public void setSun(Double sun) {
        this.sun = sun;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public List<Commodity> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<Commodity> commodities) {
        this.commodities = commodities;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
