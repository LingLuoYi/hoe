package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Spread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpreadDao extends JpaRepository<Spread,Integer> {
    Spread findByUserId(String userId);

    Spread findBySpreadPromoCode(String s);

    List<Spread> findBySpreadCode(String code);
}
