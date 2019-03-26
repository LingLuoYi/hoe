package com.henglong.cloud.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity(name = "Cloud_Assets")
public class Assets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "assets_pay_id")
    private String assetsPayId;
    //支付id，同时也是资产id

    //所有者ID
    @Column(name = "assets_user_id")
    @NotBlank(message = "UserID不能为空")
    private String assetsUserId;

    @Column(name = "assets_name")
    private String assetsName;
    //资产名称

    @Column(name = "assets_type")
    private String assetsType;
    //资产类型

    @Column(name = "assets_num")
    private String assetsNum;
    //拥有数量

    @Column(name = "assets_term")
    private String assetsTerm;
    //资产周期，0为永久

    @Column(name = "maintain_pay_type",length = 1)
    private Integer maintainPayType;
    //维护费缴纳方式0 扣除，1 预缴

    @Column(name = "maintain_day")
    private Integer maintainDay;
    //已支付维护费天数，不得大于资产周期,-1 为扣除方式

    @Column(name = "assets_value")
    private String deductions;
    //收益扣除费用,如果是扣除，则为收益扣除费用，如果是预缴，则为预缴费用

    @Column(name = "assets_watt")
    private Double watt;
    //算力计算必须，功率

    @Column(name = "power_rate")
    private Double powerRate;
    //电费

    @Column(name = "curing")
    private Double curing;
    //算力计算必须，固化率

    @Column(name = "assets_profit",columnDefinition = "decimal(19,16)")
    private BigDecimal assetsProfit;
    //资产浮动收益

    @Column(name = "assets_all_profit",columnDefinition = "decimal(19,16)")
    private BigDecimal assetsAllProfit;
    //资产累计收益，不做提现依据

    @Column(name = "assets_frozen_profit",columnDefinition = "decimal(19,16)")
    private BigDecimal assetsFrozenProfit;
    //冻结收益

    @Column(name = "assets_available_profit",columnDefinition = "decimal(19,16)")
    private BigDecimal assetsAvailableProfit;
    //已提收益

    @Column(name = "assets_initial_value",columnDefinition = "decimal(19,16)")
    private BigDecimal InitialValue;
    //资产初始价值 InitialValue * curing + InitialValue

    @Column(name = "assets_time")
    private Date assetsTime;
    //资产开始时间

    @Column(name = "assets_day")
    private String assetsDay;
    //资产已持有天数,如果缴纳维护费天数小于持有天数3天，则暂停收益

    @Column(name = "assets_state")
    private String assetsState;
    //资产状态,1 资产欠费 2 资产已完成 3 资产以被管理员取消 4 待生效

    @Column(name = "assets_phone")
    @NotBlank(message = "联系方式不能为空")
    private String assetsPhone;
    //该笔资产联系人,原本是手机，但现在也可以使用email

    @Column(name = "assets_remark",length = 50)
    private String remark;
    //资产备注

    @Transient
    private List<Maintain> maintains;
    //维护费订单

    @Transient
    private Double cost;
    //已缴纳的维护费


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAssetsUserId() {
        return assetsUserId;
    }

    public void setAssetsUserId(String assetsUserId) {
        this.assetsUserId = assetsUserId;
    }

    public String getAssetsPayId() {
        return assetsPayId;
    }

    public void setAssetsPayId(String assetsPayId) {
        this.assetsPayId = assetsPayId;
    }

    public String getAssetsName() {
        return assetsName;
    }

    public void setAssetsName(String assetsName) {
        this.assetsName = assetsName;
    }

    public String getAssetsType() {
        return assetsType;
    }

    public void setAssetsType(String assetsType) {
        this.assetsType = assetsType;
    }

    public String getAssetsNum() {
        return assetsNum;
    }

    public void setAssetsNum(String assetsNum) {
        this.assetsNum = assetsNum;
    }

    public String getAssetsTerm() {
        return assetsTerm;
    }

    public void setAssetsTerm(String assetsTerm) {
        this.assetsTerm = assetsTerm;
    }

    public Integer getMaintainPayType() {
        return maintainPayType;
    }

    public void setMaintainPayType(Integer maintainPayType) {
        this.maintainPayType = maintainPayType;
    }

    public Integer getMaintainDay() {
        return maintainDay;
    }

    public void setMaintainDay(Integer maintainDay) {
        this.maintainDay = maintainDay;
    }

    public String getDeductions() {
        return deductions;
    }

    public void setDeductions(String deductions) {
        this.deductions = deductions;
    }

    public Double getWatt() {
        return watt;
    }

    public void setWatt(Double watt) {
        this.watt = watt;
    }

    public Double getPowerRate() {
        return powerRate;
    }

    public void setPowerRate(Double powerRate) {
        this.powerRate = powerRate;
    }

    public Double getCuring() {
        return curing;
    }

    public void setCuring(Double curing) {
        this.curing = curing;
    }

    public BigDecimal getAssetsProfit() {
        return assetsProfit;
    }

    public void setAssetsProfit(BigDecimal assetsProfit) {
        this.assetsProfit = assetsProfit;
    }

    public BigDecimal getAssetsAllProfit() {
        return assetsAllProfit;
    }

    public void setAssetsAllProfit(BigDecimal assetsAllProfit) {
        this.assetsAllProfit = assetsAllProfit;
    }

    public BigDecimal getAssetsAvailableProfit() {
        return assetsAvailableProfit;
    }

    public void setAssetsAvailableProfit(BigDecimal assetsAvailableProfit) {
        this.assetsAvailableProfit = assetsAvailableProfit;
    }

    public BigDecimal getAssetsFrozenProfit() {
        return assetsFrozenProfit;
    }

    public void setAssetsFrozenProfit(BigDecimal assetsFrozenProfit) {
        this.assetsFrozenProfit = assetsFrozenProfit;
    }

    public BigDecimal getInitialValue() {
        return InitialValue;
    }

    public void setInitialValue(BigDecimal initialValue) {
        InitialValue = initialValue;
    }

    public Date getAssetsTime() {
        return assetsTime;
    }

    public void setAssetsTime(Date assetsTime) {
        this.assetsTime = assetsTime;
    }

    public String getAssetsDay() {
        return assetsDay;
    }

    public void setAssetsDay(String assetsDay) {
        this.assetsDay = assetsDay;
    }

    public String getAssetsState() {
        return assetsState;
    }

    public void setAssetsState(String assetsState) {
        this.assetsState = assetsState;
    }

    public String getAssetsPhone() {
        return assetsPhone;
    }

    public void setAssetsPhone(String assetsPhone) {
        this.assetsPhone = assetsPhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public List<Maintain> getMaintains() {
        return maintains;
    }

    public void setMaintains(List<Maintain> maintains) {
        this.maintains = maintains;
    }
}
