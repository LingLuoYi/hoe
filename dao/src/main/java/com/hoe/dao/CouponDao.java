package com.hoe.dao;

import com.hoe.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponDao extends JpaRepository<Coupon,Integer> {
}
