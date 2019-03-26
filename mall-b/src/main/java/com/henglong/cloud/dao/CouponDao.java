package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponDao extends JpaRepository<Coupon,Integer> {
}
