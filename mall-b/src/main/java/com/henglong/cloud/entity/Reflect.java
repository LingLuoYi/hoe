package com.henglong.cloud.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "cloud_reflect")
public class Reflect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    @NotBlank(message = "UserID不能为空")
    private String userId;

    @Column(name = "reflect_assetsId")
    private String assetsId;
    //资产id

    @Column(name = "reflect_name")
    private String name;
    //提现人名称

    @Column(name = "reflect_phone")
    private String phone;
    //手机号

    @Column(name = "reflect_email")
    private String email;
    //邮箱

    @Column(name = "reflect_IDCard")
    private String IDCard;
    //身份证号码

    @Column(name = "reflect_assetsType")
    private String assetsType;
    //资产类型

    @Column(name = "reflect_num",columnDefinition = "decimal(19,10)")
    private BigDecimal num;
    //提现数量

    @Column(name = "reflect_wallet")
    private String wallet;
    //提现到的钱包

    @Column(name = "reflect_status")
    private String state;
    //状态0 完成，已打币  1 成功，未打币 2 未通过 3 审核中

    @Column(name = "reflect_remarks")
    private String remarks;
    //备注

    @Column(name = "reflect_submit_time")
    private Date submitTime;
    //提交时间

    @Column(name = "examine_user_id",length = 50)
    private String examineUserId;
    //审核人ID

    @Column(name = "examine_phone")
    private String examinePhone;
    //审核联系方式

    @Column(name = "examine_time")
    private Date examineTime;
    //审核时间

    @Column(name = "reflect_hash",columnDefinition = "TEXT")
    private String hash;
    //转账hash

    @Column(name = "actual_num",columnDefinition = "decimal(19,16)")
    private BigDecimal actualNum;
    //实际转账数量

    @Column(name = "brokerage",columnDefinition = "decimal(19,16)")
    private BigDecimal brokerage;
    //转账手续费

    @Column(name = "transfer_user_id",length = 50)
    private String TransferUserId;
    //转账人id

    @Column(name = "complete_time")
    private Date completeTime;
    //转账时间也是完成时间


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

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public String getAssetsType() {
        return assetsType;
    }

    public void setAssetsType(String assetsType) {
        this.assetsType = assetsType;
    }

    public BigDecimal getNum() {
        return num;
    }

    public void setNum(BigDecimal num) {
        this.num = num;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getExamineUserId() {
        return examineUserId;
    }

    public void setExamineUserId(String examineUserId) {
        this.examineUserId = examineUserId;
    }

    public String getExaminePhone() {
        return examinePhone;
    }

    public void setExaminePhone(String examinePhone) {
        this.examinePhone = examinePhone;
    }

    public Date getExamineTime() {
        return examineTime;
    }

    public void setExamineTime(Date examineTime) {
        this.examineTime = examineTime;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public BigDecimal getActualNum() {
        return actualNum;
    }

    public void setActualNum(BigDecimal actualNum) {
        this.actualNum = actualNum;
    }

    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    public String getTransferUserId() {
        return TransferUserId;
    }

    public void setTransferUserId(String transferUserId) {
        TransferUserId = transferUserId;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }
}
