package com.henglong.cloud.entity.other;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 每单每天明细
 */
@Entity(name = "other_assets_day")
public class AssetsDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String userId;

    @Column(length = 50)
    private String assetsId;
    //资产号

    @Column
    private Date time;

    private Integer term;
    //资产周期

    private Integer validMaintainDay;
    //资产可维护天数

    @Column(columnDefinition = "decimal(19,16)")
    private BigDecimal profit;
    //收益

    @Column(columnDefinition = "decimal(19,16)")
    private BigDecimal powerRate;
    //电费

    @Column(length = 50)
    private String kuangchi;
    //矿池



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

    public String getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(String assetsId) {
        this.assetsId = assetsId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Integer getValidMaintainDay() {
        return validMaintainDay;
    }

    public void setValidMaintainDay(Integer validMaintainDay) {
        this.validMaintainDay = validMaintainDay;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getPowerRate() {
        return powerRate;
    }

    public void setPowerRate(BigDecimal powerRate) {
        this.powerRate = powerRate;
    }

    public String getKuangchi() {
        return kuangchi;
    }

    public void setKuangchi(String kuangchi) {
        this.kuangchi = kuangchi;
    }
}
