package com.hoe.entity.other;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 每日明细
 */
@Entity(name = "other_assets_date")
public class AssetsDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String userId;

    private Date time;

    @Column(columnDefinition = "decimal(19,16)")
    private BigDecimal profit;

    @Column(columnDefinition = "decimal(19,16)")
    private BigDecimal powerRate;

    @Column(length = 50)
    private String kuangchi;

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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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
