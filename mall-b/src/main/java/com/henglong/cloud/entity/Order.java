package com.henglong.cloud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity(name = "Cloud_Order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //购买者id
    @Column(name = "user_id",length = 50)
    @NotBlank(message = "UserID不能为空")
    private String userId;

    //订单id
    @Column(name = "order_Id",unique =true,length = 50)
    @NotBlank(message = "订单id不能为空")
    private String orderId;

    //商品id
    @Column(name = "order_commodity_id",length = 50)
    @NotBlank(message = "商品id不能为空")
    private String orderCommodityId;

    //商品名称
    @Column(name = "order_Commodity_Name",length = 50)
    private String orderCommodityName;

    //商品类别，1 BTC,ETH
    @Column(name = "order_Commodity_Type",length = 5)
    private String orderCommodityType;

    //订单名称
    @Column(name = "order_Name",length = 50)
    private String orderName;

    //订单金额
    @Column(name = "order_Money",length = 50)
    private String orderMoney;

    //商品数量
    @Column(name = "order_Num",length = 50)
    private String orderNum;

    //订单使用优惠劵
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id",referencedColumnName = "id")
    private UserCoupon userCoupon;

    //订单期限
    @Column(name = "order_Term",length = 10)
    private String orderTerm;

    //订单开始时间
    @Column(name = "order_Start_Time",length = 50)
    private String orderStartTime;

    //订单结束时间
    @Column(name = "order_Stop_Time",length = 50)
    private String orderStopTime;

    //0 完成，订单状态
    @Column(name = "order_State",length = 10)
    private String orderState;

    //购买人名字
    @Column(name = "name",length = 10)
    private String name;

    //购买人联系手机
    @Column(name = "phone",length = 20)
    private String phone;

    //购买人联系邮箱
    @Column(name = "email",length = 50)
    private String email;

    //订单维护费支付方式0 扣除，1 预缴
    @Column(name = "maintain_pay_type",length = 1)
    private Integer maintainPayType;

    //0为已完成支付，支付状态
    @Column(name = "order_pay_start",length = 1)
    private Integer payStart;

    @Column(name = "order_pay_type",length = 20)
    private String payType;

    @Column(name ="pay_id")
    private String payId;

    //实物订单物流信息
    @Column(name = "express_num",length = 50)
    private String expressNum;

    //实物订单收货地址
    @OneToOne(targetEntity = Address.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id",referencedColumnName = "id")
    private Address address;

    //订单生成时间
    @Column(name = "order_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderTime;


    //订单取消原因
    @Column(name = "cancel_Reason",length = 50)
    private String cancelReason;


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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderMoney() {
        return orderMoney;
    }

    public UserCoupon getUserCoupon() {
        return userCoupon;
    }

    public void setUserCoupon(UserCoupon userCoupon) {
        this.userCoupon = userCoupon;
    }

    public void setOrderMoney(String orderMoney) {
        this.orderMoney = orderMoney;
    }

    public String getOrderTerm() {
        return orderTerm;
    }

    public void setOrderTerm(String orderTerm) {
        this.orderTerm = orderTerm;
    }

    public String getOrderStartTime() {
        return orderStartTime;
    }

    public void setOrderStartTime(String orderStartTime) {
        this.orderStartTime = orderStartTime;
    }

    public String getOrderStopTime() {
        return orderStopTime;
    }

    public void setOrderStopTime(String orderStopTime) {
        this.orderStopTime = orderStopTime;
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

    public String getOrderCommodityId() {
        return orderCommodityId;
    }

    public void setOrderCommodityId(String orderCommodityId) {
        this.orderCommodityId = orderCommodityId;
    }

    public String getOrderCommodityName() {
        return orderCommodityName;
    }

    public void setOrderCommodityName(String orderCommodityName) {
        this.orderCommodityName = orderCommodityName;
    }

    public String getOrderCommodityType() {
        return orderCommodityType;
    }

    public void setOrderCommodityType(String orderCommodityType) {
        this.orderCommodityType = orderCommodityType;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getMaintainPayType() {
        return maintainPayType;
    }

    public void setMaintainPayType(Integer maintainPayType) {
        this.maintainPayType = maintainPayType;
    }

    public Integer getPayStart() {
        return payStart;
    }

    public void setPayStart(Integer payStart) {
        this.payStart = payStart;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public void setExpressNum(String expressNum) {
        this.expressNum = expressNum;
    }

    public String getExpressNum() {
        return expressNum;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
