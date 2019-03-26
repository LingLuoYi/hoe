package com.henglong.cloud.dao;

import com.henglong.cloud.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponDao extends JpaRepository<UserCoupon,String> {

    UserCoupon findByUserIdAndUserCouponId(String userId,String userCouponId);
}
