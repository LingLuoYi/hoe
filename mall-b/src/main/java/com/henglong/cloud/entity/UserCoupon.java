package com.henglong.cloud.entity;

import javax.persistence.*;

/**
 * 用户优惠劵
 */
@Entity(name = "Cloud_user_coupon")
public class UserCoupon {

    //优惠劵id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_coupon_id",length = 50)
    private String userCouponId;

    //所有者id
    @Column(name = "user_id",length = 50)
    private String userId;

    //优惠劵规则
    @ManyToOne(targetEntity = Coupon.class)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    //优惠劵状态,0使用，1未使用
    @Column(name = "state",length = 10)
    private Integer state;

    //备注
    @Column(name = "remark",length = 50)
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(String userCouponId) {
        this.userCouponId = userCouponId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
