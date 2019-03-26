package com.henglong.cloud.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Cloud_Commodity")
public class Commodity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "commodity_id")
    private String commodityId;

    //商品名称
    @Column(name = "commodity_Name")
    private String commodityName;

    //商品国家分类，固定字符串，ZH US
    @Column(name = "commodity_Country",length = 20)
    private String commodityCountry;

    //现有库存，必须是整数
//    @JsonIgnore
    @Column(name = "commodity_Stock")
    private String commodityStock;

    //总库存，必须是整数
//    @JsonIgnore
    @Column(name = "commodity_initial_Stock")
    private String commodityInitialStock;

    //商品属性，购买下限 0，不限制
    @Column(name = "commodity_present")
    private Integer commodityPresent;

    //商品属性，购买上限 0，不限制
    @Column(name = "commodity_top_Limit")
    private Integer commodityTopLimit;

    //商品属性，购买限制 0，不限制
    @Column(name = "commodity_Limit")
    private Integer commodityLimit;


    //商品单价
    @Column(name = "commodity_Money")
    private String commodityMoney;

    //商品类型，1实物商品，BTC，ETH为算力商品
    @Column(name = "commodity_Type")
    private String commodityType;

    //算力属性，单位瓦特
    @Column(name = "commodity_watt")
    private Double commodityWatt;

    //算力属性，单位电费
    @Column(name = "commodity_power_rate")
    private Double commodityPowerRate;

    //算力属性，固化率 默认 -1
    @Column(name = "commodity_curing")
    private Double commodityCuring;

    //交割时间，如果是0，则当天交割
    @Column(name = "commodity_time")
    private String commodityTime;

    //期限，如果为0，则是永久商品，期限商品则是交割之日起开始计算。
    @Column(name = "commodity_term")
    private String commodityTerm;

    //商品描述文字，支持html
    @Column(name = "commodity_Describe",columnDefinition = "MEDIUMTEXT")
    private String commodityDescribe;

    //商品图片链接
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable( name="commodity_url")
    @OrderColumn
    @Column(name = "commodity_url")
    private List<String> commodityUrl = new ArrayList<>();

    //商品状态 0 正常出售 1 预热商品 2 下架 3 删除
    @Column(name = "commodity_state",length = 2)
    private Integer commodityState;

    @Transient
    private Integer HHHHH;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityCountry() {
        return commodityCountry;
    }

    public void setCommodityCountry(String commodityCountry) {
        this.commodityCountry = commodityCountry;
    }

    public String getCommodityStock() {
        return commodityStock;
    }

    public void setCommodityStock(String commodityStock) {
        this.commodityStock = commodityStock;
    }

    public String getCommodityInitialStock() {
        return commodityInitialStock;
    }

    public void setCommodityInitialStock(String commodityInitialStock) {
        this.commodityInitialStock = commodityInitialStock;
    }

    public Integer getCommodityLimit() {
        return commodityLimit;
    }

    public void setCommodityLimit(Integer commodityLimit) {
        this.commodityLimit = commodityLimit;
    }

    public Integer getCommodityPresent() {
        return commodityPresent;
    }

    public void setCommodityPresent(Integer commodityPresent) {
        this.commodityPresent = commodityPresent;
    }

    public Integer getCommodityTopLimit() {
        return commodityTopLimit;
    }

    public void setCommodityTopLimit(Integer commodityTopLimit) {
        this.commodityTopLimit = commodityTopLimit;
    }

    public String getCommodityMoney() {
        return commodityMoney;
    }

    public void setCommodityMoney(String commodityMoney) {
        this.commodityMoney = commodityMoney;
    }

    public String getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(String commodityType) {
        this.commodityType = commodityType;
    }

    public Double getCommodityPowerRate() {
        return commodityPowerRate;
    }

    public void setCommodityPowerRate(Double commodityPowerRate) {
        this.commodityPowerRate = commodityPowerRate;
    }

    public Double getCommodityWatt() {
        return commodityWatt;
    }

    public void setCommodityWatt(Double commodityWatt) {
        this.commodityWatt = commodityWatt;
    }

    public Double getCommodityCuring() {
        return commodityCuring;
    }

    public void setCommodityCuring(Double commodityCuring) {
        this.commodityCuring = commodityCuring;
    }

    public String getCommodityTime() {
        return commodityTime;
    }

    public void setCommodityTime(String commodityTime) {
        this.commodityTime = commodityTime;
    }

    public void setCommodityDescribe(String commodityDescribe) {
        this.commodityDescribe = commodityDescribe;
    }

    public String getCommodityDescribe() {
        return commodityDescribe;
    }

    public List<String> getCommodityUrl() {
        return commodityUrl;
    }

    public void setCommodityUrl(List<String> commodityUrl) {
        this.commodityUrl = commodityUrl;
    }

    public String getCommodityTerm() {
        return commodityTerm;
    }

    public void setCommodityTerm(String commodityTerm) {
        this.commodityTerm = commodityTerm;
    }

    public void setCommodityState(Integer commodityState) {
        this.commodityState = commodityState;
    }

    public Integer getCommodityState() {
        return commodityState;
    }


    public Integer getHHHHH() {
        return HHHHH;
    }

    public void setHHHHH(Integer HHHHH) {
        this.HHHHH = HHHHH;
    }
}
