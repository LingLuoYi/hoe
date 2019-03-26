package com.hoe.dao;

import com.hoe.entity.Commodity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CommodityDao extends JpaRepository<Commodity,Integer>, JpaSpecificationExecutor {
   Commodity findByCommodityId(String id);
   List<Commodity> findByCommodityName(String name);
   List<Commodity> findByCommodityType(String type);

   List<Commodity> findByCommodityState(Integer state);

//   @Query(value = "select id, commodity_country, commodity_describe, commodity_id, commodity_initial_stock, commodity_maintain, commodity_money, commodity_name, commodity_power_rate, commodity_state, commodity_stock, commodity_term, commodity_time, commodity_type, commodity_watt from cloud_commodity where commodity_state = 0 and commodity_name like '%'",nativeQuery = true)
   Page<Commodity> findByCommodityNameContaining(String name, Pageable pageable);

   Page<Commodity> findByCommodityId(String id, Pageable p);
}
