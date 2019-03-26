package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderDao extends JpaRepository<Order,Integer>{

    List<Order> findByUserId(String userId);

    List<Order> findByUserIdAndOrderCommodityId(String userId,String commodityId);

    Page<Order> findByOrderState(String state, Pageable pageable);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findByOrderCommodityType(String type,Pageable pageable);

    Order findByOrderIdAndUserId(String id , String userId);

    Order findByOrderId(String orderId);

    Page<Order> findByUserId(String userId, Pageable p);//全部

    Page<Order> findByUserIdAndOrderState(String userId, String state, Pageable p);//根据订单状态查看

    Page<Order> findByUserIdAndOrderStateAndOrderCommodityType(String userId,String state,String type,Pageable p);

    Page<Order> findByUserIdAndOrderCommodityType(String userId,String type ,Pageable p);//更具商品类型查看

    Page<Order> findByUserIdAndMaintainPayType(String userId,Integer type,Pageable p);//根据维护费类型查看

    Long countByUserId(String userId);

    Long countByUserIdAndMaintainPayType(String userId,Integer type);

    Long countByUserIdAndOrderCommodityType(String userId,String type);

    Long countByUserIdAndOrderState(String userId,String state);

    Long countByUserIdAndOrderStateAndOrderCommodityType(String userId,String start,String type);

}
