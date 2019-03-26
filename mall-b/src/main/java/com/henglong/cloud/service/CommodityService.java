package com.henglong.cloud.service;

import com.henglong.cloud.dao.*;
import com.henglong.cloud.entity.*;
import com.henglong.cloud.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 商品处理类
 */
@Service
public class CommodityService {

    private static final Logger log = LoggerFactory.getLogger(CommodityService.class);

    private final ChangeToPinYin changeToPinYin;

    private final OnlyId onlyId;

    private final DES des;

    private final CommodityDao commodityDao;

    private final OrderDao orderDao;

    private final UserDao userDao;

    private final MaintainDao maintainDao;

    private final UserCouponDao userCouponDao;

    private final ConfigDao configDao;

    @Autowired
    public CommodityService(ChangeToPinYin changeToPinYin, OnlyId onlyId, DES des, CommodityDao commodityDao, OrderDao orderDao, UserDao userDao, MaintainDao maintainDao, UserCouponDao userCouponDao, ConfigDao configDao) {
        this.changeToPinYin = changeToPinYin;
        this.onlyId = onlyId;
        this.des = des;
        this.commodityDao = commodityDao;
        this.orderDao = orderDao;
        this.userDao = userDao;
        this.maintainDao = maintainDao;
        this.userCouponDao = userCouponDao;
        this.configDao = configDao;
    }


    /**
     * 商品写入加密
     *
     * @param commodity
     * @return
     * @throws UnsupportedEncodingException
     */
    @Transactional(rollbackFor = Exception.class)
    public Json CommodityAdd(Commodity commodity) throws UnsupportedEncodingException {
        Config config = configDao.findById(1).get();
        log.info("写入商品开始");
        BASE64Decoder decoder = new BASE64Decoder();
        //数据验证区
        if (commodity.getCommodityMoney() == null || "".equals(commodity.getCommodityMoney()))
            return API.error(MessageUtils.get("commodity.univalent.expire"));
        if (commodity.getCommodityStock() == null || "".equals(commodity.getCommodityStock()))
            return API.error(MessageUtils.get("commodity.stock.expire"));
        if (commodity.getCommodityName() == null || "".equals(commodity.getCommodityName()))
            return API.error(MessageUtils.get("commodity.name.expire"));
        if (commodity.getCommodityType() == null || "".equals(commodity.getCommodityType()))
            return API.error(MessageUtils.get("commodity.type.expire"));
        if (commodity.getCommodityInitialStock() == null || "".equals(commodity.getCommodityInitialStock()))
            return API.error(MessageUtils.get("commodity.initial.stock.expire"));
        if (!Regular.isEntity(commodity))
            return API.error("输入的参数不合法");
        if (Integer.valueOf(commodity.getCommodityStock()) > Integer.valueOf(commodity.getCommodityInitialStock()))
            return API.error("商品现库存不能超过总库存");
        //验证结束
        Commodity commodity1 = new Commodity();
        if (commodity.getCommodityId() == null || "".equals(commodity.getCommodityId())) {
            String id = onlyId.generateRefID(changeToPinYin.getStringPinYin(commodity.getCommodityName()));
            commodity1.setCommodityId(id);
            log.info("商品ID【" + id + "】");
        } else {
            //验证商品id是否合法
            if (commodityDao.findByCommodityId(commodity.getCommodityId()) == null)
                return API.error(MessageUtils.get("currency.parameter.error"));
            commodity1.setId(commodity.getId());
            commodity1.setCommodityId(commodity.getCommodityId());
            log.info("ID【" + commodity.getId() + "】");
            log.info("商品ID【" + commodity.getCommodityId() + "】");
        }
        commodity1.setCommodityName(commodity.getCommodityName());

        if (commodity.getCommodityCountry() != null && !"".equals(commodity.getCommodityCountry()))
            commodity1.setCommodityCountry(commodity.getCommodityCountry());
        else
            commodity1.setCommodityCountry("ZH");

        commodity1.setCommodityMoney(new BASE64Encoder().encode(des.encrypt(commodity.getCommodityMoney().getBytes(config.getCoding()), config.getDesPass())));
        commodity1.setCommodityStock(new BASE64Encoder().encode(des.encrypt(commodity.getCommodityStock().getBytes(config.getCoding()), config.getDesPass())));
        commodity1.setCommodityInitialStock(new BASE64Encoder().encode(des.encrypt(commodity.getCommodityInitialStock().getBytes(config.getCoding()), config.getDesPass())));
        commodity1.setCommodityType(commodity.getCommodityType());
        if (commodity.getCommodityTerm() !=null && !"".equals(commodity.getCommodityTerm()))
            commodity1.setCommodityTerm(commodity.getCommodityTerm());
        else
            commodity1.setCommodityTerm("0");
        if (commodity.getCommodityWatt() != null)
            commodity1.setCommodityWatt(commodity.getCommodityWatt());
        else
            commodity1.setCommodityWatt(0.0);
        if (commodity.getCommodityPowerRate() != null)
            commodity1.setCommodityPowerRate(commodity.getCommodityPowerRate());
        else
            commodity1.setCommodityPowerRate(0.0);
        if (commodity.getCommodityUrl() != null)
            commodity1.setCommodityUrl(commodity.getCommodityUrl());
        else
            commodity1.setCommodityUrl(new ArrayList<>());
        if (commodity.getCommodityState() != null)
            commodity1.setCommodityState(commodity.getCommodityState());
        else
            commodity1.setCommodityState(2);
        if (commodity.getCommodityPresent() != null)
            commodity1.setCommodityPresent(commodity.getCommodityPresent());
        else
            commodity1.setCommodityPresent(0);
        if (commodity.getCommodityTopLimit() != null)
            commodity1.setCommodityTopLimit(commodity.getCommodityTopLimit());
        else
            commodity1.setCommodityTopLimit(0);
        if (commodity.getCommodityLimit() != null)
            commodity1.setCommodityLimit(commodity.getCommodityLimit());
        else
            commodity1.setCommodityLimit(0);
        if ((commodity.getCommodityCuring() != null) && (-1 != commodity.getCommodityCuring())) {
            commodity1.setCommodityCuring(commodity.getCommodityCuring());
        }else {
            commodity1.setCommodityCuring(-1.0);
        }
        commodity1.setCommodityCountry("ZH");//目前固定
        commodity1.setCommodityTime(commodity.getCommodityTime());
        commodity1.setCommodityDescribe(commodity.getCommodityDescribe());
        commodity1.setHHHHH(commodity.getHHHHH());
        //图片上传
        commodityDao.save(commodity1);
        log.info("写入商品完成");
        return API.Success(commodity1);
    }

    /**
     * 查询全部商品(解密)
     *
     * @param
     * @return
     * @throws Exception
     */
    public Json CommodityAllRead(String id) throws Exception {
        Config config = configDao.findById(1).get();
        if (!Regular.isSql(id))
            return API.error("ID不合法");
        List<Commodity> listAll = new ArrayList<>();
        List<Commodity> listAll2 = new ArrayList<>();
        BASE64Decoder decoder = new BASE64Decoder();
        if (id != null && !"".equals(id)){
            listAll.add(commodityDao.findByCommodityId(id));
        }else {
            listAll = commodityDao.findAll();
        }
        for (Commodity aListAll : listAll) {
            aListAll.setCommodityStock(new String(des.decrypt(decoder.decodeBuffer(aListAll.getCommodityStock()), config.getDesPass())));
            aListAll.setCommodityInitialStock(new String(des.decrypt(decoder.decodeBuffer(aListAll.getCommodityInitialStock()), config.getDesPass())));
            aListAll.setCommodityMoney(new String(des.decrypt(decoder.decodeBuffer(aListAll.getCommodityMoney()), config.getDesPass())));
            listAll2.add(aListAll);
        }
        return API.Success(listAll2);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    public Json CommodityPage(Integer page,Integer size,String name,String id) throws Exception {
        if (!Regular.isSql(name)){
            return API.error("参数不正确");
        }
        Config config = configDao.findById(1).get();
        BASE64Decoder decoder = new BASE64Decoder();
        if (page == null){
            page = 0;
        }else {
            page = page - 1;
        }
        if (size == null || 0 == size)
            size =10;
        Pageable pageable = PageRequest.of(page,size);
        List<Commodity> list = new ArrayList<>();
        Page<Commodity> commodityPage = null;
        if (name != null && !"".equals(name)){
            commodityPage = commodityDao.findByCommodityNameContaining(name,pageable);
        }else if (id != null && !"".equals(id)){
            commodityPage = commodityDao.findByCommodityId(id,pageable);
        } else{
            commodityPage = commodityDao.findAll(pageable);
        }
        assert commodityPage != null;
        for (Commodity commodity:commodityPage) {
            commodity.setCommodityStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), config.getDesPass())));
            commodity.setCommodityInitialStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityInitialStock()), config.getDesPass())));
            commodity.setCommodityMoney(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()), config.getDesPass())));
            list.add(commodity);
        }
        return API.Success(list);
    }

    /**
     * 状态查询
     * @param state
     * @return
     * @throws Exception
     */
    public Json CommodityState(Integer state) throws Exception {
        Config config = configDao.findById(1).get();
        BASE64Decoder decoder = new BASE64Decoder();
        List<Commodity> commodityList = commodityDao.findByCommodityState(state);
        List<Commodity> list = new ArrayList<>();
        for (Commodity commodity:commodityList) {
            commodity.setCommodityStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), config.getDesPass())));
            commodity.setCommodityInitialStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityInitialStock()), config.getDesPass())));
            commodity.setCommodityMoney(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()), config.getDesPass())));
            list.add(commodity);
        }
        return API.Success(list);
    }

    //购买商品
    @Transactional(rollbackFor = Exception.class)
    public Json CommodityPurchase(String id, String num,Integer address_id,Integer maintainPayType,Integer term,String userCouponId) throws Exception {
        if (num.length() >= 10)
            return API.error("商品数量不正确");
        if (!Regular.isSql(userCouponId))
            return API.error("优惠劵参数不合法");
        if (!Regular.isSql(id))
            return API.error("商品ID不合法");
        if (!Regular.isSql(num))
            return API.error("商品数量不合法");
        Config config = configDao.findById(1).get();
        //获取登录用户
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        //创建用户
        User user = userDao.findByUserId(userId);
        if (user == null) {
            log.warn("购买方法，购买用户【" + userId + "】不存在");
            return API.error(MessageUtils.get("user.unregistered"));
        }
        log.info("开始生成用户【"+userId+"】的订单信息");
        BASE64Decoder decoder = new BASE64Decoder();
        //创建订单实体
        Order order = new Order();
        //获取商品信息
        Commodity commodity = commodityDao.findByCommodityId(id);

        if (commodity == null) {
            log.warn("用户【"+userId+"】，未查询到商品【" + id + "】信息！");
            return API.error(MessageUtils.get("commodity.expire"));
        }
        if (commodity.getCommodityState() == 1)
            return API.error(MessageUtils.get("commodity.unsold"));
        if (commodity.getCommodityState() == 2)
            return API.error(MessageUtils.get("commodity.lower.shelf"));
        if (commodity.getCommodityState() == 3)
            return API.error(MessageUtils.get("commodity.existent"));
        //验证账户购买资格
        if (commodity.getCommodityLimit() != 0) {
            List<Order> orders = orderDao.findByUserIdAndOrderCommodityId(userId, commodity.getCommodityId());
            if (orders !=null) {
                int i = 0;
                for (Order order1 : orders) {
                    if (!"18".equals(order1.getOrderState()))
                        i++;
                }
                if (commodity.getCommodityLimit() < i)
                    return API.error("每个用户限购" + commodity.getCommodityLimit() + "哦");
            }
        }
        if (commodity.getCommodityPresent() > Integer.valueOf(num) && commodity.getCommodityPresent() != 0)
            return API.error("未达到起售数量");
        if (commodity.getCommodityTopLimit() < Integer.valueOf(num) && commodity.getCommodityTopLimit() != 0)
            return API.error("超过最大购买限制");
        //查询产品库存
        int x = Integer.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), config.getDesPass())));
        log.info("用户【"+userId+"】请求生成订单，查询到库存信息" + x);
        if (x > Integer.valueOf(num) && Integer.valueOf(num) > 0) {
            //写入订单信息
            order.setUserId(userId);
            order.setName(user.getName());
            order.setEmail(user.getEmail());
            order.setPhone(user.getPhone());
            order.setOrderCommodityId(id);
            order.setOrderCommodityName(commodity.getCommodityName());
            order.setOrderId("CY"+onlyId.OrderId());
            order.setOrderCommodityType(commodity.getCommodityType());
            //订单价格计算,暂时忽略其他版本的商品，统一人民币
            if (maintainPayType == null){
                maintainPayType = 1;//默认支付方式
            }
            if (commodity.getCommodityCuring() != -1){//如果是固化收益，则不计算维护费
                maintainPayType = 0;
            }
            if (maintainPayType == 0) {
                Double a = Double.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()), config.getDesPass())));
                Double b = Double.valueOf(num);
                Double c = a * b;
                ////////////////////优惠劵/////////////////////
                if (userCouponId == null) {
                    order.setOrderMoney(String.valueOf(MoneyUtil.formatMoney(c)));//订单金额，不使用优惠券
                }else {//如果使用优惠劵
                    UserCoupon userCoupon = userCouponDao.findByUserIdAndUserCouponId(user.getUserId(), userCouponId);
                    if (userCoupon == null)
                        return API.error("优惠券不存在");
                    //验证优惠劵规则
                    if (userCoupon.getState() == 1) {
                        Coupon coupon = userCoupon.getCoupon();
                        for (Commodity commodity1 : coupon.getCommodities()) {
                            if (commodity.getCommodityId().equals(commodity1.getCommodityId())) {//可以使用
                                if (coupon.getType() == 1) {
                                    if (c > coupon.getSun()) {
                                        c = c - coupon.getMoney();
                                    } else {
                                        API.error("当前订单不满足优惠劵使用条件");
                                    }
                                } else {
                                    //其他优惠方式
                                }
                                break;
                            }
                        }
                        userCoupon.setState(0);
                        order.setUserCoupon(userCoupon);//
                        order.setOrderMoney(String.valueOf(MoneyUtil.formatMoney(c)));//订单金额，扣除优惠劵
                    }else {
                        return API.error("优惠劵已使用");
                    }
                }
                ////////////////////////////////////////////
                order.setMaintainPayType(0);
            }else if (maintainPayType == 1){//预缴维护费,计算应收
                //计算维护费,一千瓦一小时一度电
                Double a = ((commodity.getCommodityWatt()/1000)*commodity.getCommodityPowerRate())*24;//单位维护费
                Double b = Double.valueOf(num);//订单数量
                Double c = Double.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()), config.getDesPass())));//商品单价
                if (term == null || 0 == term)
                    term = 10;//默认期限
                Double d = c * b;
                Double f = a * b * term;//判断缴费期限是否满足
                Double g = d + f;
                //生成维护费订单
                Maintain maintain = new Maintain();
                //生成维护费id
                maintain.setMaintainId("MN"+onlyId.MaintainId());
                maintain.setUserId(user.getUserId());
                maintain.setCommodityId(commodity.getCommodityId());
                //关联的订单id
                maintain.setAssetsId(order.getOrderId());
                //期限
                maintain.setTerm(term);
                maintain.setState(CodeConstant.INITIAL);
                maintain.setMoney(f);//维护费用
                ////////////////////优惠劵/////////////////////
                if (userCouponId == null) {
                    order.setOrderMoney(String.valueOf(MoneyUtil.formatMoney(g)));//订单金额，不使用优惠劵
                    order.setUserCoupon(null);
                }else {//如果使用优惠劵
                    UserCoupon userCoupon = userCouponDao.findByUserIdAndUserCouponId(user.getUserId(), userCouponId);
                    if (userCoupon == null)
                        return API.error("优惠券不存在");
                    //验证优惠劵规则
                    if (userCoupon.getState() == 1) {
                        Coupon coupon = userCoupon.getCoupon();
                        for (Commodity commodity1 : coupon.getCommodities()) {
                            if (commodity.getCommodityId().equals(commodity1.getCommodityId())) {//可以使用
                                if (coupon.getType() == 1) {
                                    if (g > coupon.getSun()) {//商品优惠
                                        g = g - coupon.getMoney();
                                    } else {
                                        API.error("当前订单不满足优惠劵使用条件");
                                    }
                                } else {
                                    //其他优惠方式
                                }
                                break;
                            }
                        }
                        userCoupon.setState(0);
                        order.setUserCoupon(userCoupon);//
                        order.setOrderMoney(String.valueOf(MoneyUtil.formatMoney(g)));//订单金额，扣除优惠劵
                    }else {
                        return API.error("优惠劵已使用");
                    }
                }
                ////////////////////////////////////////////
                order.setMaintainPayType(1);
                maintainDao.save(maintain);
            }
            order.setOrderNum(num);
            if ("1".equals(commodity.getCommodityType())) {
                if (user.getAddress() == null)
                    return API.error(MessageUtils.get("commodity.address.expire"));
                order.setOrderName("实物商品_" + user.getUserId());
                for (Address address: user.getAddress()) {
                    if (address_id != null) {
                        if (address_id.equals(address.getId())) {
                            order.setAddress(address);
                            break;
                        }
                    }else {
                        if (0 == address.getDefaults()){
                            order.setAddress(address);
                        }
                    }
                }
            }else {
                order.setOrderName(commodity.getCommodityType() + "_" + user.getUserId());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tim = sdf.format(new Date());
            //判断交货时间，如果为零写入当前时间
            if (commodity.getCommodityTime().equals("0"))
                order.setOrderStartTime(tim);
            else
                order.setOrderStartTime(commodity.getCommodityTime());
            //判断是否是永久商品，如果不是则返回期限
            if (commodity.getCommodityTerm().equals("0")) {
                order.setOrderTerm("0");
                order.setOrderStopTime("--");
            } else {
                if ("0".equals(commodity.getCommodityTime()))
                    order.setOrderStopTime(Time.TimePuls(tim, (Integer.valueOf(commodity.getCommodityTerm()))));
                else
                    order.setOrderStopTime(Time.TimePuls(commodity.getCommodityTime(), Integer.valueOf(commodity.getCommodityTerm())));
                order.setOrderTerm(commodity.getCommodityTerm());
            }
            order.setOrderTime(new Date());
            order.setOrderState(""+CodeConstant.INITIAL);
            order.setPayStart(CodeConstant.INITIAL);
            log.info("订单生成完毕");
            orderDao.save(order);
            //调用线程检查订单过期情况
            OrderTime(order.getOrderId());
            return API.Success(order);
        } else {
            log.warn("购买方法，用户使用错误的商品数量");
            return API.error(MessageUtils.get("commodity.stock.insufficient"));
        }
    }

    //根据id查询
    public Json CommodityOneForId(String id) throws Exception {
        if (id == null || "".equals(id))
            return API.error("请输入ID");
        if (!Regular.isSql(id))
            return API.error("id不合法");
        Config config = configDao.findById(1).get();
        Commodity commodity = commodityDao.findByCommodityId(id);
        if (commodity == null)
            return API.error("商品不存在");
        BASE64Decoder decoder = new BASE64Decoder();
        //解密商品
        commodity.setCommodityStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), config.getDesPass())));
        commodity.setCommodityInitialStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityInitialStock()),config.getDesPass())));
        commodity.setCommodityMoney(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()),config.getDesPass())));
        return API.Success(commodity);
    }

    //根据名字查询
    public Json CommodityForName(String name) throws Exception {
        if (!Regular.isSql(name))
            return API.error("商品名称不合法");
        Config config = configDao.findById(1).get();
        List<Commodity> list = commodityDao.findByCommodityName(name);
        List<Commodity> list1 = new ArrayList<>();
        BASE64Decoder decoder = new BASE64Decoder();
        for (Commodity commodity : list) {
            //解密商品
            commodity.setCommodityStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), config.getDesPass())));
            commodity.setCommodityInitialStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityInitialStock()), config.getDesPass())));
            commodity.setCommodityMoney(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()), config.getDesPass())));
            list1.add(commodity);
        }
        return API.Success(list1);
    }

    //根据类型查询
    public Json CommodityForType(String type) throws Exception {
        if (!Regular.isSql(type)){
            return API.error("商品类型不合法");
        }
        Config config = configDao.findById(1).get();
        List<Commodity> list = commodityDao.findByCommodityType(type);
        List<Commodity> list1 = new ArrayList<>();
        BASE64Decoder decoder = new BASE64Decoder();
        for (Commodity commodity : list) {
            //解密商品
            commodity.setCommodityStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), config.getDesPass())));
            commodity.setCommodityInitialStock(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityInitialStock()), config.getDesPass())));
            commodity.setCommodityMoney(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityMoney()), config.getDesPass())));
            list1.add(commodity);
        }
        return API.Success(list1);
    }

    //判断订单是否过期，如果过期则回退库存
    public void OrderTime(String orderId) {
        Timer timer = new Timer();
        timer.schedule(new Task(timer,orderId),new Date(),5000);
    }

    class Task extends TimerTask{

        private Timer timer;

        private String orderId;

        public Task(Timer timer , String orderId) {
            this.timer = timer;
            this.orderId=orderId;
        }

        @Override
        public void run() {
            BASE64Decoder decoder = new BASE64Decoder();
//            log.info("当前时间【"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"】");
            Order order = orderDao.findByOrderId(orderId);
            if (order == null) {
                log.warn("不存在的订单【"+orderId+"】");
                this.timer.cancel();
            }
            //判断订单状态
            assert order != null;
            if ((""+CodeConstant.PAYMENT).equals(order.getOrderState()) || (""+CodeConstant.SUCCESS).equals(order.getOrderState())) {
                log.info("订单【"+orderId+"】已生成支付订单");
                this.timer.cancel();
            }
            //判断订单是否过期
            if (Time.belongDate(new Date(), order.getOrderTime(), 30)) {
                //过期操作
                    //改变订单状态为过期
                    order.setOrderState(""+CodeConstant.CLOSE);
                    orderDao.save(order);
                log.info("订单【"+orderId+"】已过期");
                this.timer.cancel();
            }
        }
    }
//    @Scheduled(cron = "0/2 * * * * *")
//    public void OrderTime2(){
//        log.info("循环输出");
//    }

}
