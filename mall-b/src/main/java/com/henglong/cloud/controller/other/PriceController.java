package com.henglong.cloud.controller.other;

import com.henglong.cloud.dao.*;
import com.henglong.cloud.entity.*;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
@CrossOrigin(allowCredentials="true")
@RequestMapping("/hoe")
public class PriceController {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(PriceController.class);

    private final CommodityDao commodityDao;

    private final CouponDao couponDao;

    private final ConfigDao configDao;

    private final OrderDao orderDao;

    private final PayDao payDao;

    @Autowired
    public PriceController(CommodityDao commodityDao, CouponDao couponDao, ConfigDao configDao, OrderDao orderDao, PayDao payDao) {
        this.commodityDao = commodityDao;
        this.couponDao = couponDao;
        this.configDao = configDao;
        this.orderDao = orderDao;
        this.payDao = payDao;
    }

    /**
     *计算维护费
     * @param commodityID 要计算的商品id
     * @param type 输出金额类型，1 人民币  0 BTC
     * @param  num 维护费天数
     * @return
     */
    @RequestMapping("/maintain_calculation")
    public Json Maintain(@RequestParam String commodityID, @RequestParam Integer num, Integer type){
        Config config = configDao.findById(1).get();
        if (type == null)
            type = 1;
        Commodity commodity = commodityDao.findByCommodityId(commodityID);
        if (commodity == null)
            return API.error("要计算的商品不存在");
        //计算维护费
        Double powerRate = commodity.getCommodityPowerRate() * (commodity.getCommodityWatt()/1000) * Double.valueOf(num) * 24;//一天的电费
        //换算成当前币值
        if (type == 0) {
            BigDecimal BTCPowerRate = new BigDecimal(powerRate / config.getBtcExchange());//模拟值
            return API.Success(BTCPowerRate);
        }
        return API.Success(powerRate);
    }

    /**
     * 商品价格计算
     * @param commodityID 要计算的商品id
     * @param num 商品数量
     * @param couponID 优惠劵规则id
     * @return
     */
    @RequestMapping("/price")
    public Json Price(@RequestParam String commodityID,@RequestParam Integer num,Integer couponID){
        //查询商品
        Commodity commodity = commodityDao.findByCommodityId(commodityID);
        Double price = 0.0;
        if (commodity == null)
            return API.error("要计算的商品不存在");
        //计算商品价格
        if (couponID == null || 0 == couponID){
            price = Double.valueOf(commodity.getCommodityMoney()) * Double.valueOf(num);
        }else {
            Optional<Coupon> couponOptional = couponDao.findById(couponID);
            if (!couponOptional.isPresent())
                return API.error("优惠劵规则不存在");
            Coupon coupon = couponOptional.get();
            //校验优惠劵规则
            if ((Double.valueOf(commodity.getCommodityMoney()) * Double.valueOf(num)) > coupon.getSun()){
                price = Double.valueOf(commodity.getCommodityMoney()) * Double.valueOf(num) - coupon.getMoney();
            }else {
                return API.error("不满足优惠劵使用条件");
            }

        }
        return API.Success(price);
    }

    /**
     * btc转人民币或人民币转btc
     * @param type 转换类型 1 0
     * @param num 转换数量人民币精确到分
     * @return
     */
    @RequestMapping("/transformation")
    public Json Transformation(@RequestParam("type") Integer type,@RequestParam BigDecimal num){
        Config config = configDao.findById(1).get();
        BigDecimal j = new BigDecimal("0");
        if (type == 0){
            if (num.compareTo(new BigDecimal("0.01")) == -1)
                return API.error("人民币最小单位是分，且不能等于0.01");
            j = num.divide(new BigDecimal(config.getBtcExchange()),16, BigDecimal.ROUND_DOWN);
        }else if (type == 1){
            j = new BigDecimal(config.getBtcExchange()).multiply(num).setScale(2, BigDecimal.ROUND_DOWN);
        }
        return API.Success(j);
    }

    /**
     * 订单超时计算
     * @param id 订单id
     * @return
     */
    @RequestMapping("/order_time")
    public Json orderTime(String id){
        if (id != null && !"".equals(id)){
            Order order = orderDao.findByOrderId(id);
            if (order == null) {
                return API.error("要计算的订单不存在");
            }else {
                if ("14".equals(order.getOrderState())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date now = new Date();
                    //下单时间
                    Date orderTime = order.getOrderTime();
                    Date afterDate = new Date(orderTime.getTime() + 1800000);
                    return API.Success(Time.diffHours(now, afterDate));
                } else {
                    return API.error("订单已经不需要在计算了");
                }
            }
        }else {
            return API.error("请输入正确的参数");
        }
    }

    @RequestMapping("/pay_time")
    public Json PayTime(String id){
        if (id != null && !"".equals(id)){
            Optional<Pay> pay = payDao.findByPayId(id);
            if (!pay.isPresent()) {
                return API.error("要计算的订单不存在");
            }else {
                Pay pay1 = pay.get();
                if ("14".equals(pay1.getPayState()) || "19".equals(pay1.getPayState())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date now = new Date();
                    //下单时间
                    Date orderTime = pay1.getPayTime();
                    Date afterDate = new Date(orderTime.getTime() + 86400000);
                    return API.Success(afterDate.getTime() - now.getTime());
                } else {
                    return API.error("订单已经不需要在计算了");
                }
            }
        }else {
            return API.error("请输入正确的参数");
        }
    }
}