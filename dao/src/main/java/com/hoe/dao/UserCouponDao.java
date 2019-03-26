package com.hoe.dao;

import com.hoe.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponDao extends JpaRepository<UserCoupon,String> {

    UserCoupon findByUserIdAndUserCouponId(String userId, String userCouponId);
}
