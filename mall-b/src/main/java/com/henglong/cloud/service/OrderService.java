package com.henglong.cloud.service;

import com.henglong.cloud.dao.*;
import com.henglong.cloud.entity.*;
import com.henglong.cloud.util.*;
import org.apache.shiro.SecurityUtils;
import org.aspectj.bridge.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 生成订单在商品类
 */
@Service
public class OrderService {

    private final OrderDao orderDao;

    private final PayDao payDao;

    private final CommodityDao commodityDao;

    private final DES des;

    private final ConfigDao configDao;

    @Autowired
    public OrderService(OrderDao orderDao, PayDao payDao, CommodityDao commodityDao, DES des, ConfigDao configDao) {
        this.orderDao = orderDao;
        this.payDao = payDao;
        this.commodityDao = commodityDao;
        this.des = des;
        this.configDao = configDao;
    }

    //获取当前用户订单信息
    public Json OrderInfo(){
        //获取登录用户
        String userId=(String) SecurityUtils.getSubject().getPrincipal();
        List<Order> orders = orderDao.findByUserId(userId);
        return API.Success(orders);
    }

    /**
     *
     * @param index 页面索引
     * @param size 页面显示数量
     * @param orderState 根据状态查询
     * @param orderCommodityType 更具商品类型查询
     * @return
     */
    public Json OrderInfoPage(Integer index,Integer size,Integer orderState,String orderCommodityType,Integer maintainPayType){
        if (!Regular.isSql(orderCommodityType))
            return API.error("参数不正确");
        if (index == null )
            index = 0;
        if (size == null || 0 == size)
            size =10;
        Map<String,Object> map = new HashMap<>();
        String userId=(String) SecurityUtils.getSubject().getPrincipal();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(index,size,sort);
        List<Order> list = new ArrayList<>();
        Page<Order> orderPage = null;
        if (orderState != null && orderCommodityType == null){
            orderPage = orderDao.findByUserIdAndOrderState(userId,""+orderState,pageable);
            map.put("count",orderDao.countByUserIdAndOrderState(userId,""+orderState));
        }else if (orderCommodityType != null && orderState == null){
            orderPage = orderDao.findByUserIdAndOrderCommodityType(userId,orderCommodityType,pageable);
            map.put("count",orderDao.countByUserIdAndOrderCommodityType(userId,orderCommodityType));
        }else if (maintainPayType != null){
            orderPage = orderDao.findByUserIdAndMaintainPayType(userId,maintainPayType,pageable);
            map.put("count",orderDao.countByUserIdAndMaintainPayType(userId,maintainPayType));
        }else if (orderCommodityType != null && orderState != null){
            orderPage = orderDao.findByUserIdAndOrderStateAndOrderCommodityType(userId,""+orderState,orderCommodityType,pageable);
            map.put("count",orderDao.countByUserIdAndOrderStateAndOrderCommodityType(userId,""+orderState,orderCommodityType));
        }else {
            orderPage = orderDao.findByUserId(userId, pageable);
            map.put("count",orderDao.countByUserId(userId));
        }
        for (Order order:orderPage){
            list.add(order);
        }
        map.put("list",list);
        return API.Success(map);
    }

    //获取单个订单（用户）
    public Json OrderOneInfo(String id){
        if (!Regular.isSql(id))
            return API.error("参数错误");
        //获取登录用户
        String userId=(String) SecurityUtils.getSubject().getPrincipal();
        Order order = orderDao.findByOrderIdAndUserId(id,userId);
        return API.Success(order);
    }

    /**
     * 关闭订单
     * @param orderId 关闭的订单id
     * @param s 关闭的原因
     * @return
     */
    public Json OrderClose(String orderId,String s) throws Exception {
        Config config = configDao.findById(1).get();
        BASE64Decoder decoder = new BASE64Decoder();
        if (!Regular.isSql(orderId))
            return API.error("参数错误");
        if (!Regular.isSql(s))
            return API.error("参数错误");
        String userId=(String) SecurityUtils.getSubject().getPrincipal();
        Order order = orderDao.findByOrderIdAndUserId(orderId,userId);
        if (order == null)
            return API.error(MessageUtils.get("order.expire"));
        order.setOrderState(""+CodeConstant.CLOSE);
        order.setPayStart(CodeConstant.CLOSE);
        order.setCancelReason(s);
        //查询是否有支付订单
        Pay pay = payDao.findByPayOrderId(orderId);
        if (pay != null){
            //更改支付订单状态
            pay.setPayState(""+CodeConstant.CLOSE);
            pay.setVoucherState(""+CodeConstant.CLOSE);
            //查询商品
            Commodity commodity = commodityDao.findByCommodityId(order.getOrderCommodityId());
            //退回产品库存
            Integer money = Integer.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), config.getDesPass())));
            Integer orderMoney = Integer.valueOf(pay.getPayNum());
            commodity.setCommodityStock(new BASE64Encoder().encode(des.encrypt(Integer.toString(money + orderMoney).getBytes(config.getCoding()), config.getDesPass())));
            commodityDao.save(commodity);
            payDao.save(pay);
        }
        orderDao.save(order);
        return API.Success(MessageUtils.get("order.close"));
    }


    //获取所有订单（管理员）
    public Json OrderAllInfo(Integer index, Integer size,String id,String state,String type){
        if (!Regular.isSql(id))
            return API.error("ID参数错误");
        if (!Regular.isSql(state))
            return API.error("状态参数错误");
        if (!Regular.isSql(type))
            return API.error("类型参数错误");
        if (index == null){
            index = 0;
        }else {
            index = index - 1;
        }
        if (size == null && 0 == size){
            size = 10;
        }
        Pageable pageable = PageRequest.of(index,size);
        List<Order> orders = new ArrayList<>();
        if (id != null && !"".equals(id)){
            orders.add(orderDao.findByOrderId(id));
        }else if (state != null && !"".equals(state)){
            Page<Order> orderPage = orderDao.findByOrderState(state,pageable);
            if (orderPage == null)
                return API.Success();
            for (Order o:orderPage) {
                orders.add(o);
            }
        }else if(type != null && !"".equals(type)){
            Page<Order> orderPage = orderDao.findByOrderCommodityType(type,pageable);
            if (orderPage == null)
                return API.Success();
            for (Order o:orderPage) {
                orders.add(o);
            }
        } else {
            Page<Order> orderPage = orderDao.findAll(pageable);
            if (orderPage == null)
                return API.Success();
            for (Order o:orderPage) {
                orders.add(o);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("size",orderDao.count());
        map.put("data",orders);
         return API.Success(map);
    }

    //根据ID查询单个订单（管理员）
    public Json OrderAdminOneInfo(String id){
        if (!Regular.isSql(id))
            return API.error("参数错误");
        return API.Success(orderDao.findByOrderId(id));
    }


    //修改订单（用户）
    public Json orderUserUpdate(String id ,String num) throws Exception {
        if (!Regular.isSql(id))
            return API.error("参数错误");
        if (!Regular.isSql(num))
            return API.error("参数错误");
        BASE64Decoder decoder = new BASE64Decoder();
        //获取当前用户登录的用户
        String userId=(String) SecurityUtils.getSubject().getPrincipal();
        //查询定单是否存在
        Order order = orderDao.findByOrderIdAndUserId(id,userId);
        if (order == null)
            return API.error(MessageUtils.get("order.expire"));
        //查询支付订单是否生成支付订单,如果存在则不允许修改
        Pay pay = payDao.findByPayOrderIdAndPayUserId(id,userId);
        if (pay != null)
            return API.error(MessageUtils.get("order.pay.error"));
        //查询商品
        Commodity commodity = commodityDao.findByCommodityId(order.getOrderCommodityId());
        if (commodity == null)
            return API.error(MessageUtils.get("commodity.existent"));
        order.setOrderNum(num);
        //计算价格，还有优惠劵没有用
        int a = Integer.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()), "szhl8888")));
        int b = Integer.valueOf(num);
        order.setOrderMoney(String.valueOf(a * b));
        orderDao.save(order);
        return API.Success(order);
    }

    //删除订单（用户）
    public Json orderUserDelete(String id){
        //获取当前用户登录的用户
//        String userId=(String) SecurityUtils.getSubject().getPrincipal();
//        //查询定单是否存在
//        Order order = orderDao.findByOrderIdAndUserId(id,userId);
//        if (order == null)
//            return API.error("订单不存在");
//        //查询支付订单是否生成支付订单,如果存在则不允许删除
//        Pay pay = payDao.findByPayOrderIdAndPayUserId(id,userId);
//        if (pay != null)
//            return API.error("当前订单不允许被删除");
//        orderDao.delete(order);
        return API.Success("该接口已经废弃");
    }

    //修改订单(管理员)
    //无规则修改
    public Json orderUpdate(Order order){
        if (!Regular.isEntity(order))
            return API.error("参数不正确");
        //查询传入订单是否存在
        if (orderDao.findByOrderId(order.getOrderId()) == null)
            return API.error(MessageUtils.get("order.close"));
        orderDao.save(order);
        return API.Success(order);
    }

    //删除订单(管理员)
    //无规则
    public Json orderDelete(String id){
        if (!Regular.isSql(id))
            return API.error("参数错误");
        Order order = orderDao.findByOrderId(id);
        if (order == null)
            return API.error(MessageUtils.get("order.close"));
        orderDao.delete(order);
        return API.Success(order);
    }
}
