package com.hoe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Entity(name = "Cloud_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id",length = 50)
    private String userId;

    @Column(name = "phone",length = 50)
    private String phone;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @JsonIgnore
    @Column(name = "salt")
    private String salt;

    @Column(name = "name",length = 50)
    private String name;

    @Email(message = "请正确输入邮箱")
    @Column(name = "email",length = 50)
    private String email;

    /*头像链接*/
    @Column(name = "img_url")
    private String imgUrl;

    /*身份证号码*/
    @Column(name = "ID_card_no",length = 100)
    private String IDCardNo;

    @Column(name = "ID_name",length = 50)
    private String IDName;

    /*身份证图片链接*/
    @ElementCollection
    private Map<String,String> IDCardImg;

    @Column(name = "user_start",length = 10)
    private Integer userStart;

    @JsonIgnore
    @Column(name = "random_code",length = 50)
    private String RandomCode;

    @Transient
    private List<String> Roles;

    //钱包
    @ElementCollection
    private Map<String,String> Wallet;

    //地址
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name="address_id")
    private List<Address> address;

    @Column(name = "profit",columnDefinition = "decimal(19,16)")
    private BigDecimal profit;
    //在本网站的累计收益


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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRandomCode() {
        return RandomCode;
    }

    public void setRandomCode(String randomCode) {
        RandomCode = randomCode;
    }

    public List<String> getRoles() {
        return Roles;
    }

    public void setRoles(List<String> roles) {
        Roles = roles;
    }

    public Map getWallet() {
        return Wallet;
    }

    public void setWallet(Map wallet) {
        Wallet = wallet;
    }

    public Map getIDCardImg() {
        return IDCardImg;
    }

    public void setIDCardImg(Map IDCardImg) {
        this.IDCardImg = IDCardImg;
    }

    public String getIDCardNo() {
        return IDCardNo;
    }

    public void setIDCardNo(String IDCardNo) {
        this.IDCardNo = IDCardNo;
    }

    public String getIDName() {
        return IDName;
    }

    public void setIDName(String IDName) {
        this.IDName = IDName;
    }

    public Integer getUserStart() {
        return userStart;
    }

    public void setUserStart(Integer userStart) {
        this.userStart = userStart;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }
}
