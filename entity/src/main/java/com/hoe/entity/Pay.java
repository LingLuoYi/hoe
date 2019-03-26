package com.hoe.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 支付流水单
 */
@Entity(name = "Cloud_Pay")
public class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //序号

    @Column(name = "pay_id",unique =true)
    private String payId;
    //支付订单id

    @Column(name = "pay_order_id")
    @NotBlank(message = "订单id不能为空")
    private String payOrderId;
    //订单id,要付款的订单

    @Column(name = "pay_commodity_id")
    @NotBlank(message = "商品id不能为空")
    private String payCommodityId;
    //商品id

    @Column(name = "pay_title",length = 50)
    private String payTitle;

    @Column(name = "pay_commodity_name")
    private String payCommodityName;
    //商品名称

    @Column(name = "pay_commodity_unit_price")
    private String payCommodityUnitPrice;
    //商品单价

    @Column(name = "pay_commodity_money")
    private String payCommodityMoney;
    //订单金额

    @Column(name = "pay_num")
    private String payNum;

    @Column(name = "pay_mode")
    private String payMode;
    //支付方式

    @Column(name = "pay_Type_Rate")
    private String PayTypeRate;
    //支付方式手续费

    @Column(name = "pay_Coupon_money")
    private String PayCouponMoney;
    //优惠金额

    @Column(name = "pay_receipts")
    private String payReceipts;
    //实收款

    @Column(name = "pay_type_id")
    private String PayTypeId;
    //第三方支付id，银行卡为转账单号

    @Column(name = "pay_user_id")
    @NotBlank(message = "UserId不能为空")
    private String payUserId;
    //支付人ID

    @Column(name = "pay_name")
    private String payName;
    //支付人姓名

    @Column(name = "pay_phone")
    private String payPhone;
    //支付人手机号

    @Column(name ="pay_email")
    private String payEmail;
    //支付人邮箱

    @Column(name = "pay_state")
    private String payState;
    //订单状态 0 完成

    @Column(name = "pay_voucher_state")
    private String voucherState;
    //付款凭证状态 0 审核通过 1 上传待审核 2 审核未通过

    @Column(name = "pay_voucher_url")
    private String voucherUrl;
    //凭证url

    @Column(name = "pay_time")
    private Date payTime;
    //支付订单生成时间

    @Column(name = "examine_user_id",length = 50)
    private String userId;
    //审核者id

    @Column(name = "examine_Time")
    private Date examineTime;
    //审核时间


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public String getPayCommodityId() {
        return payCommodityId;
    }

    public void setPayCommodityId(String payCommodityId) {
        this.payCommodityId = payCommodityId;
    }

    public String getPayTitle() {
        return payTitle;
    }

    public void setPayTitle(String payTitle) {
        this.payTitle = payTitle;
    }

    public String getPayCommodityName() {
        return payCommodityName;
    }

    public void setPayCommodityName(String payCommodityName) {
        this.payCommodityName = payCommodityName;
    }

    public String getPayCommodityUnitPrice() {
        return payCommodityUnitPrice;
    }

    public void setPayCommodityUnitPrice(String payCommodityUnitPrice) {
        this.payCommodityUnitPrice = payCommodityUnitPrice;
    }

    public String getPayCommodityMoney() {
        return payCommodityMoney;
    }

    public void setPayCommodityMoney(String payCommodityMoney) {
        this.payCommodityMoney = payCommodityMoney;
    }

    public String getPayReceipts() {
        return payReceipts;
    }

    public void setPayReceipts(String payReceipts) {
        this.payReceipts = payReceipts;
    }

    public String getPayTypeRate() {
        return PayTypeRate;
    }

    public void setPayTypeRate(String payTypeRate) {
        PayTypeRate = payTypeRate;
    }

    public String getPayCouponMoney() {
        return PayCouponMoney;
    }

    public void setPayCouponMoney(String payCouponMoney) {
        PayCouponMoney = payCouponMoney;
    }

    public String getPayNum() {
        return payNum;
    }

    public void setPayNum(String payNum) {
        this.payNum = payNum;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getPayTypeId() {
        return PayTypeId;
    }

    public void setPayTypeId(String payTypeId) {
        PayTypeId = payTypeId;
    }

    public String getPayUserId() {
        return payUserId;
    }

    public void setPayUserId(String payUserId) {
        this.payUserId = payUserId;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getPayPhone() {
        return payPhone;
    }

    public void setPayPhone(String payPhone) {
        this.payPhone = payPhone;
    }

    public String getPayEmail() {
        return payEmail;
    }

    public void setPayEmail(String payEmail) {
        this.payEmail = payEmail;
    }

    public String getPayState() {
        return payState;
    }

    public void setPayState(String payState) {
        this.payState = payState;
    }

    public String getVoucherState() {
        return voucherState;
    }

    public void setVoucherState(String voucherState) {
        this.voucherState = voucherState;
    }

    public String getVoucherUrl() {
        return voucherUrl;
    }

    public void setVoucherUrl(String voucherUrl) {
        this.voucherUrl = voucherUrl;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getExamineTime() {
        return examineTime;
    }

    public void setExamineTime(Date examineTime) {
        this.examineTime = examineTime;
    }
}
