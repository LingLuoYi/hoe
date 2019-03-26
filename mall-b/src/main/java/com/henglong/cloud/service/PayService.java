package com.henglong.cloud.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.henglong.cloud.dao.*;
import com.henglong.cloud.entity.*;
import com.henglong.cloud.util.*;
import org.jdom.JDOMException;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PayService {

    private static final Logger log = LoggerFactory.getLogger(PayService.class);

    private final OrderDao orderDao;

    private final UserDao userDao;

    private final PayDao payDao;

    private final OnlyId onlyId;

    private final BankDao bankDao;

    private final CommodityDao commodityDao;

    private final DES des;

    private final MaintainDao maintainDao;

    private final AssetsDao assetsDao;

    private final ConfigDao configDao;

    @Autowired
    public PayService(OrderDao orderDao, UserDao userDao, PayDao payDao, OnlyId onlyId, BankDao bankDao, CommodityDao commodityDao, DES des, MaintainDao maintainDao, AssetsDao assetsDao, ConfigDao configDao) {
        this.orderDao = orderDao;
        this.userDao = userDao;
        this.payDao = payDao;
        this.onlyId = onlyId;
        this.bankDao = bankDao;
        this.commodityDao = commodityDao;
        this.des = des;
        this.maintainDao = maintainDao;
        this.assetsDao = assetsDao;
        this.configDao = configDao;
    }

    //商品支付方法,不计算各类优惠，订单需要保证传过来的金额是总金额
    @Transactional(rollbackFor = Exception.class)
    public Object PayFirst(String id, String type, HttpServletRequest request) throws Exception {
        if (!Regular.isSql(id))
            return API.error("参数错误");
        if (!Regular.isSql(type))
            return API.error("参数错误");
        Config config = configDao.findById(1).get();
        //获取当前购买人
        String userId=(String) SecurityUtils.getSubject().getPrincipal();
        //验证购买订单是否存在
        Order order = null;
        if (id.startsWith("CY")) {
            order = orderDao.findByOrderIdAndUserId(id, userId);
        }else if (id.startsWith("MN")){
            return API.error("请使用维护费缴费接口");
        }
        if (order == null) {
            log.warn("用户【"+userId+"】，请求的订单【"+id+"】不存在！");
            return API.error("订单【"+id+"】不存在");
        }
        if (!userId.equals(order.getUserId()))
            return API.error(MessageUtils.get("pay.user.discrepancy"));
        //验证支付订单是否过期
        if ((""+CodeConstant.CLOSE).equals(order.getOrderState())) {
            log.warn("用户【"+userId+"】请求的商品订单已过期");
            return API.error(MessageUtils.get("order.overdue"));
        }
        Commodity commodity = commodityDao.findByCommodityId(order.getOrderCommodityId());
        if (commodity == null){
            log.warn("用户【"+userId+"】，在支付方法请求的商品【"+order.getOrderCommodityId()+"】，不存在");
            return API.error(MessageUtils.get("commodity.expire"));
        }
        User user= userDao.findByUserId(userId);
        //判断当前订单是否已经存在支付订单
        //查询产品库存
        BASE64Decoder decoder = new BASE64Decoder();
        int x = Integer.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), config.getDesPass())));
        Pay pay = payDao.findByPayOrderId(id);
        //如果有直接返回已有的,如果是在线支付则调起支付
        if (pay != null){
            log.warn("用户【"+userId+"】，订单【"+id+"】已存在支付订单！");
            //如果存在并且订单有效，则调起支付
            if ("0".equals(pay.getPayState()))
                return API.error(MessageUtils.get("pay.complete"));
            if ("2".equals(pay.getPayState()))
                return API.error(MessageUtils.get("pay.expire"));
            //请求支付，已经存在支付订单
            Bank bank = bankDao.findAllByPayType(pay.getPayMode());
            switch (pay.getPayMode()) {
                case "Alipay": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    if (20000.00 <= Double.valueOf(order.getOrderMoney()))
                        return API.error(MessageUtils.get("pay.type.no"));
                    AlipayClient alipayClient = new DefaultAlipayClient(bank.getGateway(), bank.getAppID(), bank.getAppPrivateKey(), "json", "UTF-8", bank.getSpare(), "RSA2");
                    AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
//                    alipayRequest.setReturnUrl("");//支付状态同步通知地址
//                    alipayRequest.setNotifyUrl("http://"+ InetAddress.getLocalHost().getHostAddress()+"/assets/sdsdfsdfsdasdfasfsadfsadfsdfwewrwrtrqu/werterrweqcxvx/sewrsdfw");//支付状态异步通知地址,这里最好调用异步通知
                    alipayRequest.setNotifyUrl("http://47.88.158.203/assets/sdsdfsdfsdasdfasfsadfsadfsdfwewrwrtrqu/werterrweqcxvx/sewrsdfw");
                    int goods_type = 0;
                    if ("1".equals(order.getOrderCommodityType())){
                        goods_type = 1;
                    }
                    alipayRequest.setBizContent("{" +
                            "    \"out_trade_no\":\""+order.getOrderId()+"\"," +
                            "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                            "    \"total_amount\":"+order.getOrderMoney()+"," +
                            "    \"subject\":\"BITCENTRE "+order.getOrderCommodityName()+"\"," +
                            "    \"qr_pay_mode\": \"4\","+
                            "    \"qrcode_width\": \"150\","+
                            "    \"goods_type\": \""+goods_type+"\""+
                            "  }");//支付信息Json
                    String form = "";
                    try {
                        form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
                        if (!"".equals(form)) {
                            form = form.replace("\"","'");
                            form = form.replace("\n","");
                            log.info("{}",form);
                            return API.Success(form);
                        } else {
                            return API.error(MessageUtils.get("pay.alipay.no"));
                        }
                    } catch (AlipayApiException e) {
                        e.printStackTrace();
                    }
                }
                case "WeChatPay": {//微信支付
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    if (20000.00 <= Double.valueOf(order.getOrderMoney()))
                        return API.error(MessageUtils.get("pay.type.no"));
                    SortedMap<String, Object> map = new TreeMap<>();
                    map.put("appid", bank.getAppID());
                    map.put("mch_id", bank.getAppPrivateKey());
                    map.put("nonce_str", onlyId.RandomString(30));
                    map.put("body", "BITCENTRE-" + order.getOrderCommodityName());
                    map.put("out_trade_no", order.getOrderId());
                    map.put("total_fee", MoneyUtil.formatWeChatMoney(Double.valueOf(order.getOrderMoney())));//订单的价格必须精确到分

                    map.put("spbill_create_ip", IPUtils.getRealIP(request));
                    map.put("notify_url", "http://"+ InetAddress.getLocalHost().getHostAddress()+"/assets/sdfjsaksjadghushfnxcsdfksdjafhusahdf/sfsdfasifhx/qwexfdse");//异步通知地址

                    map.put("trade_type", "NATIVE--Native");
                    map.put("sign", Result.createSign("UTF-8", map, bank.getSpare()));
                    //调用微信统一下单接口
                    String requestXml = XmlUtil.getRequestXml(map);
                    String result = HttpUtil.doPost(bank.getGateway(), requestXml);
                    //获取状态
                    Map<Object, Object> resultMap = XmlUtil.doXMLParse(result);
                    assert resultMap != null;
                    String return_code = (String) resultMap.get("return_code");
                    String result_code = (String) resultMap.get("result_code");
                    if (return_code.contains("SUCCESS")) {
                        if (result_code.contains("SUCCESS")) {
                            return API.Success(resultMap.get("code_url"));
                        } else {
                            return API.error(MessageUtils.get("pay.wechatpay.no"));
                        }
                    } else {
                        return API.error(MessageUtils.get("pay.wechatpay.no"));
                    }
                }
                case "remit": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    Map<String,String> map = new HashMap<>();
                    map.put("open_bank",bank.getGateway());
                    map.put("name",bank.getAppID());
                    map.put("bank_id",bank.getAppPrivateKey());
                    map.put("currency",bank.getSpare());
                    map.put("sum",order.getOrderMoney());
                    map.put("msg",pay.getPayId());
                    return API.Success(map);
                }
                case "bitRemit": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    Map<String,String> map = new HashMap<>();
                    map.put("bank_id",bank.getAppPrivateKey());
                    map.put("sum",String.format("%.8f",Double.valueOf(order.getOrderMoney())/config.getBtcExchange()));
                    map.put("msg",pay.getPayId());
                    //改变订单状态
                    order.setOrderState(""+CodeConstant.PAYMENT);
                    //改变订单状态
                    order.setPayStart(CodeConstant.PAY);
                    //保存
                    orderDao.save(order);
                    payDao.save(pay);
                    OrderTime(pay.getPayId());
                    return API.Success(map);
                }
                default:
                    return API.error(MessageUtils.get("pay.type.no.1"));
            }
        }else if (x > Integer.valueOf(order.getOrderNum()) && Integer.valueOf(order.getOrderNum()) > 0){//库存充足，可以付款
            log.info("用户【"+userId+"】，商品订单【"+id+"】开始生成支付订单");
            pay = new Pay();
            //支付订单中，扣除库存
            commodity.setCommodityStock(new BASE64Encoder().encode(des.encrypt(String.valueOf(x - Integer.valueOf(order.getOrderNum())).getBytes(), config.getDesPass())));
            commodityDao.save(commodity);
            //开始写入支付订单
            pay.setPayId(onlyId.PayId());
            pay.setPayOrderId(id);
            pay.setPayCommodityId(order.getOrderCommodityId());
            pay.setPayTitle("购买云算力套餐");
            pay.setPayCommodityName(order.getOrderCommodityName());
            pay.setPayCommodityUnitPrice(""+(Double.valueOf(order.getOrderMoney())/Integer.valueOf(order.getOrderNum())));
            pay.setPayCommodityMoney(order.getOrderMoney());
            pay.setPayNum(order.getOrderNum());
            pay.setPayUserId(user.getUserId());
            pay.setPayName(user.getName());
            pay.setPayPhone(user.getPhone());
            pay.setPayEmail(user.getEmail());
            pay.setPayState(""+CodeConstant.INITIAL);
            pay.setVoucherState("2");
            pay.setPayTime(new Date());
            pay.setPayState(""+CodeConstant.INITIAL);
            pay.setVoucherUrl("");
            pay.setPayMode(type);
            //请求支付,创建新的支付流水单
            Bank bank = bankDao.findAllByPayType(type);
            order.setPayType(type);
            switch (type) {
                case "Alipay": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    if (20000.00 <= Double.valueOf(order.getOrderMoney()))
                        return API.error(MessageUtils.get("pay.type.no"));
                    AlipayClient alipayClient = new DefaultAlipayClient(bank.getGateway(), bank.getAppID(), bank.getAppPrivateKey(), "json", "UTF-8", bank.getSpare(), "RSA2");
                    AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
//                    alipayRequest.setReturnUrl("");//支付状态同步通知地址
//                    alipayRequest.setNotifyUrl("http://"+ InetAddress.getLocalHost().getHostAddress()+"/assets/sdsdfsdfsdasdfasfsadfsadfsdfwewrwrtrqu/werterrweqcxvx/sewrsdfw");//支付状态异步通知地址,这里最好调用异步通知
                    alipayRequest.setNotifyUrl("http://47.88.158.203/assets/sdsdfsdfsdasdfasfsadfsadfsdfwewrwrtrqu/werterrweqcxvx/sewrsdfw");
                    int goods_type = 0;
                    if ("1".equals(order.getOrderCommodityType())){
                        goods_type = 1;
                    }
                    alipayRequest.setBizContent("{" +
                            "    \"out_trade_no\":\""+order.getOrderId()+"\"," +
                            "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                            "    \"total_amount\":"+order.getOrderMoney()+"," +
                            "    \"subject\":\"BITCENTRE "+order.getOrderCommodityName()+"\"," +
                            "    \"qr_pay_mode\": \"4\","+
                            "    \"qrcode_width\": \"150\","+
                            "    \"goods_type\": \""+goods_type+"\""+
                            "  }");//支付信息Json
                    String form = "";
                    try {
                        form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
                        if (!"".equals(form)) {
                            form = form.replace("\"","'");
                            form = form.replace("\n","");
                            //改变订单状态
                            order.setOrderState(""+CodeConstant.PAYMENT);
                            //改变订单状态
                            order.setPayStart(CodeConstant.PAY);
                            //保存
                            orderDao.save(order);
                            payDao.save(pay);
                            notice.purchaseNotice(userId,order.getOrderId(),order.getOrderCommodityId(),"Alipay");
                            OrderTime(pay.getPayId());
                            return API.Success(form);
                        } else {
                            return API.error(MessageUtils.get("pay.alipay.no"));
                        }
                    } catch (AlipayApiException e) {
                        e.printStackTrace();
                    }
                }
                case "WeChatPay": {//微信支付
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    if (20000.00 <= Double.valueOf(order.getOrderMoney()))
                        return API.error(MessageUtils.get("pay.type.no"));
                    SortedMap<String, Object> map = new TreeMap<>();
                    map.put("appid", bank.getAppID());
                    map.put("mch_id", bank.getAppPrivateKey());
                    map.put("nonce_str", onlyId.RandomString(30));
                    map.put("body", "BITCENTRE-" + order.getOrderCommodityName());
                    map.put("out_trade_no", order.getOrderId());
                    map.put("total_fee", MoneyUtil.formatWeChatMoney(Double.valueOf(order.getOrderMoney())));//订单的价格必须已分为单位

                    map.put("spbill_create_ip", IPUtils.getRealIP(request));
                    map.put("notify_url", "http://"+ InetAddress.getLocalHost().getHostAddress()+"/assets/sdfjsaksjadghushfnxcsdfksdjafhusahdf/sfsdfasifhx/qwexfdse");//异步通知地址

                    map.put("trade_type", "NATIVE--Native");
                    map.put("sign", Result.createSign("UTF-8", map, bank.getSpare()));
                    //调用微信统一下单接口
                    String requestXml = XmlUtil.getRequestXml(map);
                    String result = HttpUtil.doPost(bank.getGateway(), requestXml);
                    //获取状态
                    Map<Object, Object> resultMap = XmlUtil.doXMLParse(result);
                    assert resultMap != null;
                    String return_code = (String) resultMap.get("return_code");
                    String result_code = (String) resultMap.get("result_code");
                    if (return_code.contains("SUCCESS")) {
                        if (result_code.contains("SUCCESS")) {
                            //改变订单状态
                            order.setOrderState(""+CodeConstant.PAYMENT);
                            //改变订单状态
                            order.setPayStart(CodeConstant.PAY);
                            //保存
                            orderDao.save(order);
                            payDao.save(pay);
                            notice.purchaseNotice(userId,order.getOrderId(),order.getOrderCommodityId(),"WeChatPay");
                            OrderTime(pay.getPayId());
                            return API.Success(resultMap.get("code_url"));
                        } else {
                            return API.error(MessageUtils.get("pay.wechatpay.no"));
                        }
                    } else {
                        return API.error(MessageUtils.get("pay.wechatpay.no"));
                    }
                }
                case "remit": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    Map<String,String> map = new HashMap<>();
                    map.put("open_bank",bank.getGateway());
                    map.put("name",bank.getAppID());
                    map.put("bank_id",bank.getAppPrivateKey());
                    map.put("currency",bank.getSpare());
                    map.put("sum",order.getOrderMoney());
                    map.put("msg",pay.getPayId());
                    //改变订单状态
                    order.setOrderState(""+CodeConstant.PAYMENT);
                    //改变订单状态
                    order.setPayStart(CodeConstant.PAY);
                    //保存
                    orderDao.save(order);
                    payDao.save(pay);
                    notice.purchaseNotice(userId,order.getOrderId(),order.getOrderCommodityId(),"remit");
                    OrderTime(pay.getPayId());
                    return API.Success(map);
                }
                case "bitRemit": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    Map<String,String> map = new HashMap<>();
                    map.put("bank_id",bank.getAppPrivateKey());
                    map.put("sum",String.format("%.8f",Double.valueOf(order.getOrderMoney())/config.getBtcExchange()));
                    map.put("msg",pay.getPayId());
                    //改变订单状态
                    order.setOrderState(""+CodeConstant.PAYMENT);
                    //改变订单状态
                    order.setPayStart(CodeConstant.PAY);
                    //保存
                    orderDao.save(order);
                    payDao.save(pay);
                    notice.purchaseNotice(userId,order.getOrderId(),order.getOrderCommodityId(),"bitRemit");
                    OrderTime(pay.getPayId());
                    return API.Success(map);
                }
                default:
                    return API.error(MessageUtils.get("pay.type.no.1"));
            }
        }else {
            log.warn("用户【"+userId+"】，生成【"+order.getOrderId()+"】的支付订单失败，库存不足");
            return API.error(MessageUtils.get("commodity.stock.insufficient"));
        }
    }

    //维护费订单支付方法
    @Transactional(rollbackFor = Exception.class)
    public Object MaintainPay(String id,String type,String ip) throws Exception {
        if (!Regular.isSql(id))
            return API.error("参数错误");
        if (!Regular.isSql(type))
            return API.error("参数错误");
        if (!Regular.isSql(ip))
            return API.error("参数错误");
        Config config = configDao.findById(1).get();
        //获取当前购买人
        String userId=(String) SecurityUtils.getSubject().getPrincipal();
        //验证购买订单是否存在
        Optional<Maintain> maintainOptional = Optional.empty();
        if (id.startsWith("CY")) {
            return API.error("请使用订单缴费接口");
        }else if (id.startsWith("MN")){
            maintainOptional = maintainDao.findByUserIdAndMaintainId(userId,id);
        }
        if (!maintainOptional.isPresent()) {
            log.warn("用户【"+userId+"】，请求的维护费订单【"+id+"】不存在！");
            return API.error("维护费订单【"+id+"】不存在");
        }
        Maintain maintain = maintainOptional.get();
        Assets assets = assetsDao.findByAssetsPayId(maintain.getAssetsId());
        if (assets == null)
            return API.error("支付时校验资产出错");
        User user= userDao.findByUserId(userId);
        //判断当前订单是否已经存在支付订单
        Pay pay = payDao.findByPayOrderId(id);
        //如果有直接返回已有的,如果是在线支付则调起支付
        if (pay != null){
            log.warn("用户【"+userId+"】，订单【"+id+"】已存在支付订单！");
            //如果存在并且订单有效，则调起支付
            if ("0".equals(pay.getPayState()))
                return API.error(MessageUtils.get("pay.complete"));
            if ("2".equals(pay.getPayState()))
                return API.error(MessageUtils.get("pay.expire"));
            //请求支付，已经存在支付订单
            Bank bank = bankDao.findAllByPayType(pay.getPayMode());
            switch (pay.getPayMode()) {
                case "Alipay": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    if (20000.00 <= maintain.getMoney())
                        return API.error(MessageUtils.get("pay.type.no"));
                    AlipayClient alipayClient = new DefaultAlipayClient(bank.getGateway(), bank.getAppID(), bank.getAppPrivateKey(), "json", "UTF-8", bank.getSpare(), "RSA2");
                    AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
//                    alipayRequest.setReturnUrl("");//支付状态同步通知地址
//                    alipayRequest.setNotifyUrl("http://"+ InetAddress.getLocalHost().getHostAddress()+"/assets/sdsdfsdfsdasdfasfsadfsadfsdfwewrwrtrqu/werterrweqcxvx/sewrsdfw");//支付状态异步通知地址,这里最好调用异步通知
                    alipayRequest.setNotifyUrl("http://47.88.158.203/assets/sdsdfsdfsdasdfasfsadfsadfsdfwewrwrtrqu/werterrweqcxvx/sewrsdfw");
                    int goods_type = 0;
                    alipayRequest.setBizContent("{" +
                            "    \"out_trade_no\":\""+maintain.getMaintainId()+"\"," +
                            "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                            "    \"total_amount\":"+maintain.getMoney()+"," +
                            "    \"subject\":\"BITCENTRE 维护费缴纳\"," +
                            "    \"qr_pay_mode\": \"4\","+
                            "    \"qrcode_width\": \"150\","+
                            "    \"goods_type\": \""+goods_type+"\""+
                            "  }");//支付信息Json
                    String form = "";
                    try {
                        form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
                        if (!"".equals(form)) {
                            form = form.replace("\"","'");
                            form = form.replace("\n","");
                            log.info("{}",form);
                            return API.Success(form);
                        } else {
                            return API.error(MessageUtils.get("pay.alipay.no"));
                        }
                    } catch (AlipayApiException e) {
                        e.printStackTrace();
                    }
                }
                case "WeChatPay": {//微信支付
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    if (20000.00 <= maintain.getMoney())
                        return API.error(MessageUtils.get("pay.type.no"));
                    SortedMap<String, Object> map = new TreeMap<>();
                    map.put("appid", bank.getAppID());
                    map.put("mch_id", bank.getAppPrivateKey());
                    map.put("nonce_str", onlyId.RandomString(30));
                    map.put("body", "BITCENTRE-维护费缴纳" + maintain.getTerm()+"天");
                    map.put("out_trade_no", maintain.getMaintainId());
                    map.put("total_fee", MoneyUtil.formatWeChatMoney(maintain.getMoney()));//订单的价格必须精确到分

                    map.put("spbill_create_ip", ip);
                    map.put("notify_url", "http://"+ InetAddress.getLocalHost().getHostAddress()+"/assets/sdfjsaksjadghushfnxcsdfksdjafhusahdf/sfsdfasifhx/qwexfdse");//异步通知地址

                    map.put("trade_type", "NATIVE--Native");
                    map.put("sign", Result.createSign("UTF-8", map, bank.getSpare()));
                    //调用微信统一下单接口
                    String requestXml = XmlUtil.getRequestXml(map);
                    String result = HttpUtil.doPost(bank.getGateway(), requestXml);
                    //获取状态
                    Map<Object, Object> resultMap = XmlUtil.doXMLParse(result);
                    assert resultMap != null;
                    String return_code = (String) resultMap.get("return_code");
                    String result_code = (String) resultMap.get("result_code");
                    if (return_code.contains("SUCCESS")) {
                        if (result_code.contains("SUCCESS")) {
                            return API.Success(resultMap.get("code_url"));
                        } else {
                            return API.error(MessageUtils.get("pay.wechatpay.no"));
                        }
                    } else {
                        return API.error(MessageUtils.get("pay.wechatpay.no"));
                    }
                }
                case "remit": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    Map<String,String> map = new HashMap<>();
                    map.put("open_bank",bank.getGateway());
                    map.put("name",bank.getAppID());
                    map.put("bank_id",bank.getAppPrivateKey());
                    map.put("currency",bank.getSpare());
                    map.put("sum",String.valueOf(maintain.getMoney()));
                    map.put("msg",pay.getPayId());
                    return API.Success(map);
                }
                case "bitRemit": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    Map<String,String> map = new HashMap<>();
                    map.put("bank_id",bank.getAppPrivateKey());
                    map.put("sum",String.format("%.8f",maintain.getMoney()/config.getBtcExchange()));
                    map.put("msg",pay.getPayId());
                    maintain.setState(CodeConstant.PAYMENT);
                    //保存
                    maintainDao.save(maintain);
                    payDao.save(pay);
                    return API.Success(map);
                }
                default:
                    return API.error(MessageUtils.get("pay.type.no.1"));
            }
        }else{
            log.info("用户【"+userId+"】，商品订单【"+id+"】开始生成支付订单");
            pay = new Pay();
            //开始写入支付订单
            pay.setPayId(onlyId.PayId());
            pay.setPayOrderId(id);
            pay.setPayCommodityId(maintain.getAssetsId());//资产id
            pay.setPayTitle("维护费缴纳");
            pay.setPayCommodityName("维护费缴纳");
            pay.setPayCommodityUnitPrice(""+((assets.getWatt()/1000)*assets.getPowerRate())*24);
            pay.setPayCommodityMoney(String.valueOf(maintain.getMoney()));
            pay.setPayNum(String.valueOf(maintain.getTerm()));
            pay.setPayUserId(user.getUserId());
            pay.setPayName(user.getName());
            pay.setPayPhone(user.getPhone());
            pay.setPayEmail(user.getEmail());
            pay.setPayState(""+CodeConstant.INITIAL);
            pay.setVoucherState("2");
            pay.setPayTime(new Date());
            pay.setPayState(""+CodeConstant.INITIAL);
            pay.setVoucherUrl("");
            pay.setPayMode(type);
            //请求支付,创建新的支付流水单
            Bank bank = bankDao.findAllByPayType(type);
            maintain.setPayType(type);
            switch (type) {
                case "Alipay": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    if (20000.00 <= maintain.getMoney())
                        return API.error(MessageUtils.get("pay.type.no"));
                    AlipayClient alipayClient = new DefaultAlipayClient(bank.getGateway(), bank.getAppID(), bank.getAppPrivateKey(), "json", "UTF-8", bank.getSpare(), "RSA2");
                    AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
//                    alipayRequest.setReturnUrl("");//支付状态同步通知地址
//                    alipayRequest.setNotifyUrl("http://"+ InetAddress.getLocalHost().getHostAddress()+"/assets/sdsdfsdfsdasdfasfsadfsadfsdfwewrwrtrqu/werterrweqcxvx/sewrsdfw");//支付状态异步通知地址,这里最好调用异步通知
                    alipayRequest.setNotifyUrl("http://47.88.158.203/assets/sdsdfsdfsdasdfasfsadfsadfsdfwewrwrtrqu/werterrweqcxvx/sewrsdfw");
                    int goods_type = 0;
                    alipayRequest.setBizContent("{" +
                            "    \"out_trade_no\":\""+maintain.getMaintainId()+"\"," +
                            "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                            "    \"total_amount\":"+maintain.getMoney()+"," +
                            "    \"subject\":\"BITCENTRE 维护费缴纳\"," +
                            "    \"qr_pay_mode\": \"4\","+
                            "    \"qrcode_width\": \"150\","+
                            "    \"goods_type\": \""+goods_type+"\""+
                            "  }");//支付信息Json
                    String form = "";
                    try {
                        form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
                        if (!"".equals(form)) {
                            form = form.replace("\"","'");
                            form = form.replace("\n","");
                            //改变订单状态
                            maintain.setState(CodeConstant.PAYMENT);
                            //保存
                            maintainDao.save(maintain);
                            payDao.save(pay);
                            notice.purchaseNotice(userId,maintain.getMaintainId(),maintain.getCommodityId(),"Alipay");
                            return API.Success(form);
                        } else {
                            return API.error(MessageUtils.get("pay.alipay.no"));
                        }
                    } catch (AlipayApiException e) {
                        e.printStackTrace();
                    }
                }
                case "WeChatPay": {//微信支付
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    if (20000.00 <= maintain.getMoney())
                        return API.error(MessageUtils.get("pay.type.no"));
                    SortedMap<String, Object> map = new TreeMap<>();
                    map.put("appid", bank.getAppID());
                    map.put("mch_id", bank.getAppPrivateKey());
                    map.put("nonce_str", onlyId.RandomString(30));
                    map.put("body", "BITCENTRE-维护费缴纳" + maintain.getTerm()+"天");
                    map.put("out_trade_no", maintain.getMaintainId());
                    map.put("total_fee", MoneyUtil.formatWeChatMoney(maintain.getMoney()));//订单的价格必须已分为单位

                    map.put("spbill_create_ip", ip);
                    map.put("notify_url", "http://"+ InetAddress.getLocalHost().getHostAddress()+"/assets/sdfjsaksjadghushfnxcsdfksdjafhusahdf/sfsdfasifhx/qwexfdse");//异步通知地址

                    map.put("trade_type", "NATIVE--Native");
                    map.put("sign", Result.createSign("UTF-8", map, bank.getSpare()));
                    //调用微信统一下单接口
                    String requestXml = XmlUtil.getRequestXml(map);
                    String result = HttpUtil.doPost(bank.getGateway(), requestXml);
                    //获取状态
                    Map<Object, Object> resultMap = XmlUtil.doXMLParse(result);
                    assert resultMap != null;
                    String return_code = (String) resultMap.get("return_code");
                    String result_code = (String) resultMap.get("result_code");
                    if (return_code.contains("SUCCESS")) {
                        if (result_code.contains("SUCCESS")) {
                            //改变订单状态
                            maintain.setState(CodeConstant.PAYMENT);
                            //保存
                            maintainDao.save(maintain);
                            payDao.save(pay);
                            notice.purchaseNotice(userId,maintain.getMaintainId(),maintain.getCommodityId(),"WeChatPay");
                            return API.Success(resultMap.get("code_url"));
                        } else {
                            return API.error(MessageUtils.get("pay.wechatpay.no"));
                        }
                    } else {
                        return API.error(MessageUtils.get("pay.wechatpay.no"));
                    }
                }
                case "remit": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    Map<String,String> map = new HashMap<>();
                    map.put("open_bank",bank.getGateway());
                    map.put("name",bank.getAppID());
                    map.put("bank_id",bank.getAppPrivateKey());
                    map.put("currency",bank.getSpare());
                    map.put("sum",String.valueOf(maintain.getMoney()));
                    map.put("msg",pay.getPayId());
                    //改变订单状态
                    maintain.setState(CodeConstant.PAYMENT);
                    //保存
                    maintainDao.save(maintain);
                    payDao.save(pay);
                    notice.purchaseNotice(userId,maintain.getMaintainId(),maintain.getCommodityId(),"remit");
                    return API.Success(map);
                }
                case "bitRemit": {
                    if (bank == null)
                        return API.error(MessageUtils.get("bank.no"));
                    Map<String,String> map = new HashMap<>();
                    map.put("bank_id",bank.getAppPrivateKey());
                    map.put("sum",String.format("%.8f",maintain.getMoney()/config.getBtcExchange()));
                    map.put("msg",pay.getPayId());
                    maintain.setState(CodeConstant.PAYMENT);
                    //保存
                    maintainDao.save(maintain);
                    payDao.save(pay);
                    notice.purchaseNotice(userId,maintain.getMaintainId(),maintain.getCommodityId(),"bitRemit");
                    return API.Success(map);
                }
                default:
                    return API.error(MessageUtils.get("pay.type.no.1"));
            }
        }
    }

    //添加接受付款信息
    public Json AddBank(Bank bank){
        if (!Regular.isEntity(bank))
            return API.error("参数错误");
        Bank bank1 = bankDao.findAllByPayType(bank.getPayType());
        if (bank1 != null) {
//            return API.error("该方式已经有了支付信息，请选择修改");
            if (bank.getAppID()!= null && !"".equals(bank.getAppID()))
                bank1.setAppID(bank.getAppID());
            if (bank.getAppPrivateKey() != null && !"".equals(bank.getAppPrivateKey()))
                bank1.setAppPrivateKey(bank.getAppPrivateKey());
            if (bank.getGateway() != null && !"".equals(bank.getGateway()))
                bank1.setGateway(bank.getGateway());
            if (bank.getSpare() != null && !"".equals(bank.getSpare()))
                bank1.setSpare(bank.getSpare());
            bankDao.save(bank1);
        }else {
            bankDao.save(bank);
        }
        return API.Success(bank);
    }

    //查询接受付款银行信息
    public Json BankAll(){
        return API.Success(bankDao.findAll());
    }

    //修改接受付款银行信息
    public Json BankUpdate(Bank bank){
        if (!Regular.isEntity(bank))
            return API.error("参数错误");
        Optional optional = bankDao.findById(bank.getId());
        if (optional.isPresent()) {
            Bank bank1 = (Bank) optional.get();
            if (bank.getAppID() != null && !"".equals(bank.getAppID()))
                bank1.setAppID(bank.getAppID());
            if (bank.getAppPrivateKey() != null && !"".equals(bank.getAppPrivateKey()))
                bank1.setAppPrivateKey(bank.getAppPrivateKey());
            if (bank.getGateway() != null && !"".equals(bank.getGateway()))
                bank1.setGateway(bank.getGateway());
            if (bank.getSpare() != null && !"".equals(bank.getSpare()))
                bank1.setSpare(bank.getSpare());
            if (bank.getPayType() != null && !"".equals(bank.getPayType()))
                bank1.setPayType(bank.getPayType());
            bankDao.save(bank1);
            return API.Success(bank1);
        }else {
            return API.error("修改失败");
        }

    }

    //查询支付定单凭证状态
    public Json PayvoucherState(String vs){
        if (!Regular.isSql(vs))
            return API.error("参数错误");
        return API.Success(payDao.findByVoucherState(vs));
    }

    //查询支付订单状态
    public Json PayState(String s){
        if (!Regular.isSql(s))
            return API.error("参数错误");
        return API.Success(payDao.findByPayState(s));
    }

    //查询单个订单状态
    public Json PayOne(String id){
        if (!Regular.isSql(id))
            return API.error("参数错误");

        return API.Success(payDao.findByPayIdAndPayUserId(id,(String) SecurityUtils.getSubject().getPrincipal()));
    }

    //个人获取支付订单,废弃
    public Json PayUser(){
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return API.Success(payDao.findByPayUserId(userId));
    }

    //管理员查看支付订单
    public Json PayAll(Integer index, Integer size, String id,String state,String voucherState){
        if (!Regular.isSql(id))
            return API.error("参数错误");
        if (!Regular.isSql(state))
            return API.error("参数错误");
        if (!Regular.isSql(voucherState))
            return API.error("参数错误");
        if (index == null){
            index = 0;
        }else {
            index = index - 1;
        }
        if (size == null || 0 == size){
            size = 10;
        }
        Map<String,Object> map = new HashMap<>();
        Pageable pageable = PageRequest.of(index,size);
        List<Pay> pays = new ArrayList<>();
        if (id != null && !"".equals(id)){
            pays.add(payDao.findByPayId(id).get());
        }else if (state != null && !"".equals(state)){
            Page<Pay> payPage = payDao.findByPayState(state,pageable);
            if (payPage == null){
                return API.Success();
            }
            for (Pay pay:payPage){
                pays.add(pay);
            }
            map.put("size",payDao.countByPayState(state));
            map.put("data",pays);
        }else if (voucherState != null && !"".equals(voucherState)){
            Page<Pay> payPage = payDao.findByVoucherState(voucherState,pageable);
            if (payPage == null){
                return API.Success();
            }
            for (Pay pay:payPage){
                pays.add(pay);
            }
            map.put("size",payDao.countByVoucherState(voucherState));
            map.put("data",pays);
        }else {
            Page<Pay> payPage = payDao.findAll(pageable);
            if (payPage == null){
                return API.Success();
            }
            for (Pay pay:payPage){
                pays.add(pay);
            }
            map.put("size",payDao.count());
            map.put("data",pays);
        }
        return API.Success(map);
    }

    //管理员删除方法
    public Json PayDelete(String id){
        if (!Regular.isSql(id))
            return API.error("参数错误");
        //查询支付订单是否存在
        Optional<Pay> pay = payDao.findByPayId(id);
        if (pay.isPresent())
            return API.error(MessageUtils.get("pay.no"));
        payDao.delete(pay.get());
        return API.Success(pay.get());
    }

    //管理员修改方法
    public Json PayUpdate(Pay pay){
        if (!Regular.isEntity(pay))
            return API.error("参数错误");
        //查询支付订单是否存在
        if (!payDao.findByPayId(pay.getPayId()).isPresent())
            return API.error(MessageUtils.get("pay.no"));
        payDao.save(pay);
        return API.Success(pay);
    }

    /**
     * 超时检查调用方法
     * @param payId
     */
    public void OrderTime(String payId) {
        Timer timer = new Timer();
        timer.schedule(new PayTask(timer,payId),new Date(),5000);
    }

    /**
     * 订单超时检查类
     */
    class PayTask extends TimerTask{

        private Timer timer;

        private String payId;

        public PayTask(Timer timer , String payId) {
            this.timer = timer;
            this.payId=payId;
        }

        @Override
        public void run() {
            Config config = configDao.findById(1).get();
            BASE64Decoder decoder = new BASE64Decoder();
            //查询支付订单
            Optional<Pay> optionalPay = payDao.findByPayId(payId);
            if (!optionalPay.isPresent())
                this.timer.cancel();
            Pay pay = optionalPay.get();
            if ("1".equals(pay.getVoucherState()) || (""+CodeConstant.SUCCESS).equals(pay.getVoucherState()))
                this.timer.cancel();
            if (Time.belongDate(new Date(),pay.getPayTime(),1440)){
                //过期操作
                //查询订单
                Order order =  orderDao.findByOrderId(pay.getPayOrderId());
                if (order == null) {
                    pay.setPayState(""+CodeConstant.CLOSE);
                    pay.setPayTitle("该笔支付订单前置商品单不存在！");
                    payDao.save(pay);
                    this.timer.cancel();
                }
                //查询商品
                assert order != null;
                Commodity commodity = commodityDao.findByCommodityId(order.getOrderCommodityId());
                if (commodity == null) {
                    pay.setPayState(""+CodeConstant.CLOSE);
                    pay.setPayTitle("该笔支付订单前置商品单记录的商品信息不存在");
                    payDao.save(pay);
                    this.timer.cancel();
                }
                try {
                    //返回商品库存
                    //计算历史库存
                    assert commodity != null;
                    Integer money = Integer.valueOf(new String(des.decrypt(decoder.decodeBuffer(commodity.getCommodityStock()), config.getDesPass())));
                    log.info("支付订单【"+payId+"】，查询到现有库存【"+money+"】");
                    Integer orderMoney = Integer.valueOf(pay.getPayNum());
                    log.info("支付订单【"+payId+"】拥有的商品数量【"+orderMoney+"】");
                    int s = money + orderMoney;
                    commodity.setCommodityStock(new BASE64Encoder().encode(des.encrypt(Integer.toString(s).getBytes(config.getCoding()), config.getDesPass())));
                    log.info("订单【"+payId+"】返回后的库存【"+s+"】");
                    //改变订单状态
                    order.setOrderState(""+CodeConstant.CLOSE);
                    //改变支付订单状态
                    pay.setPayState(""+CodeConstant.CLOSE);
                    payDao.save(pay);
                    orderDao.save(order);
                    commodityDao.save(commodity);
                } catch (Exception e) {
                    log.info("商品解密失败！");
                }

                log.info("支付订单【"+payId+"】超时");
                this.timer.cancel();
            }
        }
    }

}
