package com.henglong.cloud.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "cloud_put_forward")
public class PutForward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "put_assets_pay_id")
    private String assetsPayId;

    @Column(name = "put_type",length = 10)
    private String type;

    @Column(name = "put_wallet",length = 50)
    private String wallet;

    @Column(name = "put_num")
    private BigDecimal num;

    @Column(name = "put_name",length = 10)
    private String name;

    @Column(name = "phone",length = 12)
    private String phone;

    @Column(name = "put_email",length = 50)
    private String email;

    @Column(name = "put_sub_time")
    private Date submissionTime;

    @Column(name = "put_state",length = 1)
    private Integer state;

    @Column(name = "put_adopt_time")
    private Date adoptTime;

    @Column(name = "put_hash")
    private String hash;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getAssetsId() {
        return assetsPayId;
    }

    public void setAssetsId(String assetsId) {
        this.assetsPayId = assetsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public BigDecimal getNum() {
        return num;
    }

    public void setNum(BigDecimal num) {
        this.num = num;
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

    public Date getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(Date submissionTime) {
        this.submissionTime = submissionTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getAdoptTime() {
        return adoptTime;
    }

    public void setAdoptTime(Date adoptTime) {
        this.adoptTime = adoptTime;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
