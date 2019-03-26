package com.henglong.cloud.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.henglong.cloud.Handler.ResponseHandler;
import com.henglong.cloud.dao.*;
import com.henglong.cloud.dao.other.AssetsDateDao;
import com.henglong.cloud.dao.other.AssetsDayDao;
import com.henglong.cloud.entity.*;
import com.henglong.cloud.entity.other.AssetsDate;
import com.henglong.cloud.entity.other.AssetsDay;
import com.henglong.cloud.service.other.AssetsDateServer;
import com.henglong.cloud.service.other.AssetsDayServer;
import com.henglong.cloud.util.*;
import com.henglong.cloud.util.aop.aopName.Income;
import com.henglong.cloud.util.aop.aopName.RequestLimit;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 资产处理类
 */
@Service
public class AssetsService {

    private static final Logger log = LoggerFactory.getLogger(AssetsService.class);

    private final AssetsDao assetsDao;

    private final PayDao payDao;

    private final OrderDao orderDao;

    private final CommodityDao commodityDao;

    private final BankDao bankDao;

    private final MaintainDao maintainDao;

    private final ReflectDao reflectDao;

    private final ConfigDao configDao;

    @Autowired
    private AssetsDayDao assetsDayDao;

    @Autowired
    private AssetsDateDao assetsDateDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    public AssetsService(AssetsDao assetsDao, PayDao payDao, OrderDao orderDao, CommodityDao commodityDao, BankDao bankDao, MaintainDao maintainDao, ReflectDao reflectDao, ConfigDao configDao) {
        this.assetsDao = assetsDao;
        this.payDao = payDao;
        this.orderDao = orderDao;
        this.commodityDao = commodityDao;
        this.bankDao = bankDao;
        this.maintainDao = maintainDao;
        this.reflectDao = reflectDao;
        this.configDao = configDao;
    }


    /**
     * 个人资产订单查询
     * @return
     */
    @Income
    public Json AssetsOneAllInfo(){
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return API.Success(assetsDao.findByAssetsUserId(userId));
    }

    /**
     * 个人资产分页查询
     * @param index
     * @param size
     * @param state
     * @return
     */
//    @Income
    public Json AssetsPage(Integer index,Integer size,String state){
        String userId = (String)SecurityUtils.getSubject().getPrincipal();
        if (index == null)
            index = 0;
        if (size == null || 0 == size)
            size = 10;
        if (!Regular.isSql(state)){
            return API.error("错误的状态码");
        }
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(index,size,sort);
        Page<Assets> assetsPage = null;
        Map<String,Object> map = new HashMap<>();
    if (state != null && !"".equals(state)){
        assetsPage = assetsDao.findByAssetsUserIdAndAssetsState(userId,state,pageable);
        map.put("count",assetsDao.countByAssetsUserIdAndAssetsState(userId,state));
    }else {
        assetsPage = assetsDao.findByAssetsUserId(userId,pageable);
        map.put("count",assetsDao.countByAssetsUserId(userId));
    }
    List<Assets> assetsList = new ArrayList<>();
        for (Assets assets:assetsPage) {
            assetsList.add(assets);
        }
        map.put("list",assetsList);
        return API.Success(map);
    }

    /**
     * 个人资产算力合计
     * @return
     */
    public Json AssetsAllTotal(){
        Map<String,Double> map = new HashMap<>();
        Double j = 0.0;
        Double i = 0.0;
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        List<Assets> assetsList = assetsDao.findByAssetsUserId(userId);
        for (Assets assets : assetsList) {
            if ("BTC".equals(assets.getAssetsType())){
                j = j + Double.valueOf(assets.getAssetsNum());
            }else if ("ETH".equals(assets.getAssetsType())){
                i = i + Double.valueOf(assets.getAssetsNum());
            }
        }
        map.put("BTC",j);
        map.put("ETH",i);
        return API.Success(map);
    }


    /**
     * 个人总收益计算
     * @return
     */
    public Json AssetsProfitTotal(){
        Map<String,BigDecimal> map = new HashMap<>();
        BigDecimal i = new BigDecimal("0");
        BigDecimal j = new BigDecimal("0");
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        List<Assets> assetsList = assetsDao.findByAssetsUserId(userId);
        for (Assets assets:assetsList){
            if ("BTC".equals(assets.getAssetsType())){
                j = j.add(assets.getAssetsProfit());
            }else if ("ETH".equals(assets.getAssetsType())){
                i = i.add(assets.getAssetsProfit());
            }
        }
        map.put("BTC",j);
        map.put("ETH",i);
        return API.Success(map);
    }

    /**
     * 冻结收益计算
     * @return
     */
    public Json AssetsFrozenProfitTotal(){
        Map<String,BigDecimal> map = new HashMap<>();
        BigDecimal i = new BigDecimal("0");
        BigDecimal j = new BigDecimal("0");
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        List<Assets> assetsList = assetsDao.findByAssetsUserId(userId);
        for (Assets assets:assetsList){
            if ("BTC".equals(assets.getAssetsType())){
                j = j.add(assets.getAssetsFrozenProfit());
            }else if ("ETH".equals(assets.getAssetsType())){
                i = i.add(assets.getAssetsFrozenProfit());
            }
        }
        map.put("BTC",j);
        map.put("ETH",i);
        return API.Success(map);
    }

    /**
     * 合计
     * @return
     */
    @Income
    public Json Three(){
        Map<String,Map> smap = new HashMap();
        //冻结收益
        Map<String,BigDecimal> map = new HashMap<>();
        BigDecimal i = new BigDecimal("0");
        BigDecimal j = new BigDecimal("0");
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        List<Assets> assetsList = assetsDao.findByAssetsUserId(userId);
        for (Assets assets:assetsList){
            if ("BTC".equals(assets.getAssetsType())){
                if (!"3".equals(assets.getAssetsState()))
                  j = j.add(assets.getAssetsFrozenProfit());
            }else if ("ETH".equals(assets.getAssetsType())){
                i = i.add(assets.getAssetsFrozenProfit());
            }
        }
        map.put("DBTC",j);
        map.put("DETH",i);
        smap.put("D",map);
        //个人总收益
        Map<String,BigDecimal> map1 = new HashMap<>();
        BigDecimal i1 = new BigDecimal("0");
        BigDecimal j1 = new BigDecimal("0");
        List<Assets> assetsList1 = assetsDao.findByAssetsUserId(userId);
        for (Assets assets:assetsList1){
            if ("BTC".equals(assets.getAssetsType())){
                if (!"3".equals(assets.getAssetsState()))
                   j1 = j1.add(assets.getAssetsProfit());
            }else if ("ETH".equals(assets.getAssetsType())){
                i1 = i1.add(assets.getAssetsProfit());
            }
        }
        map1.put("ZBTC",j1);
        map1.put("ZETH",i1);
        smap.put("Z",map1);
        //个人算力
        Map<String,Double> map2 = new HashMap<>();
        Double j2 = 0.0;
        Double i2 = 0.0;
        List<Assets> assetsList2 = assetsDao.findByAssetsUserId(userId);
        for (Assets assets : assetsList2) {
            if ("BTC".equals(assets.getAssetsType())){
                if (!"2".equals(assets.getAssetsState()) && !"3".equals(assets.getAssetsState()))
                   j2 = j2 + Double.valueOf(assets.getAssetsNum());
            }else if ("ETH".equals(assets.getAssetsType())){
                i2 = i2 + Double.valueOf(assets.getAssetsNum());
            }
        }
        map2.put("SBTC",j2);
        map2.put("SETH",i2);
        smap.put("S",map2);
        //个人不变的收益
        Map<String,BigDecimal> map3 = new HashMap<>();
        BigDecimal i3 = new BigDecimal("0");
        BigDecimal j3 = new BigDecimal("0");
        List<Assets> assetsList3 = assetsDao.findByAssetsUserId(userId);
        for (Assets assets:assetsList3){
            if ("BTC".equals(assets.getAssetsType())){
                if (!"2".equals(assets.getAssetsState()) && !"3".equals(assets.getAssetsState()))
                   j3 = j3.add(assets.getAssetsAllProfit());
            }else if ("ETH".equals(assets.getAssetsType())){
                i3 = i3.add(assets.getAssetsAllProfit());
            }
        }
        map3.put("CBTC",j3);
        map3.put("CETH",i3);
        smap.put("C",map3);
        return API.Success(smap);
    }

    /**
     * 个人资产单条
     * @param id
     * @return
     */
    public Json AssetsForById(String id,Integer index, Integer size){
        if (!Regular.isSql(id)){
            return API.error("id有误");
        }
        if (index == null)
            index = 0;
        if (size == null || 0 == size)
            size = 10;
        Double s = 0.0;
        String userid = (String) SecurityUtils.getSubject().getPrincipal();
        Assets assets = assetsDao.findByAssetsPayIdAndAssetsUserId(id,userid);
        Pageable pageable = PageRequest.of(index,size);
        if (assets == null)
            return API.error("获取资产出错");
        try {
            List<Maintain> maintains = maintainDao.findByUserIdAndAssetsId(userid, assets.getAssetsPayId());
            List<Maintain> maintains1 = new ArrayList<>();
            for (Maintain maintain :
                    maintainDao.findByUserIdAndAssetsIdAndState(userid, assets.getAssetsPayId(), 0, pageable)) {
                maintains1.add(maintain);
            }
            assets.setMaintains(maintains1);
            for (Maintain maintain : maintains) {
                if (maintain.getState() == 0) {
                    s = s + maintain.getMoney();
                }
            }
            assets.setCost(s);
        }catch (Exception e){
            log.error("获取个人资产单条出错");
        }
        return API.Success(assets);
    }

    /**
     * 个人资产更新
     * （弃用）改用注解方式更新
     */
    public void AssetsUpdate() {
        //获取登录用户
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        //获取用户资产信息
        List<Assets> assets = assetsDao.findByAssetsUserId(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tim = sdf.format(new Date());
        //对每一条资产信息做处理
        for (Assets ass:assets){
            //判断当前资产是否有期限
            if("0".equals(ass.getAssetsTerm())){
                //判断是否到达开始时间
                if (Time.belongDate(new Date(),ass.getAssetsTime(),0))
                    //计算时间
                    ass.setAssetsDay(""+Time.differentDays(ass.getAssetsTime(),new Date()));
            }else {
                //判断是否到达开始时间
                if (Time.belongDate(new Date(),ass.getAssetsTime(),0))
                //判断是否在时间段内
                   if(!Time.belongDate(new Date(),ass.getAssetsTime(),Integer.valueOf(ass.getAssetsTerm())))
                       ass.setAssetsDay(""+Time.differentDays(ass.getAssetsTime(),new Date()));
            }
            assetsDao.save(ass);
        }

    }

    //微信支付回调接受,返回数据通知微信
    public String WeChatExamine(HttpServletRequest request, HttpServletResponse response) throws Exception{
        log.info("以下是微信支付处理日志");
        Bank bank = bankDao.findAllByPayType("WeChatPay");
        ServletInputStream instream = request.getInputStream();
        StringBuffer sb = new StringBuffer();
        int len = -1;
        byte[] buffer = new byte[1024];

        while((len = instream.read(buffer)) != -1){
            sb.append(new String(buffer,0,len));
        }
        instream.close();

        SortedMap<String,String> map = XmlUtil.doXMLParseWithSorted(sb.toString());//接受微信的通知参数
        Map<String,Object> return_data = new HashMap<String,Object>();

        //创建支付应答对象
        ResponseHandler resHandler = new ResponseHandler(request, response);

        resHandler.setAllparamenters(map);
        resHandler.setKey(bank.getSpare());

        //判断签名
        if(resHandler.isTenpaySign()){
            if(!map.get("return_code").toString().equals("SUCCESS")){
                return_data.put("return_code", "FAIL");
                return_data.put("return_msg", "return_code不正确");
            }else{
                if(!map.get("result_code").toString().equals("SUCCESS")){
                    return_data.put("return_code", "FAIL");
                    return_data.put("return_msg", "result_code不正确");
                }



                String out_trade_no = map.get("out_trade_no").toString();
                String transaction_id = map.get("transaction_id").toString();
                String total_fee = map.get("total_fee").toString();
                Pay pay = payDao.findByPayOrderId(out_trade_no);

                if(pay == null){
                    return_data.put("return_code", "FAIL");
                    return_data.put("return_msg", "订单不存在");
                    log.info("用户支付订单【"+out_trade_no+"】不存在，如果存在争议，请使用此id查询");
                    notice.payNotice("微信",out_trade_no,0);
                    return XmlUtil.getRequestXml(return_data);
                }

                //如果订单已经支付返回错误
                if("0".equals(pay.getPayState())){
                    return_data.put("return_code", "SUCCESS");
                    return_data.put("return_msg", "OK");
                    log.info("订单-"+out_trade_no+"  :已经支付了");
                    return XmlUtil.getRequestXml(return_data);
                }

                int m = Integer.valueOf(pay.getPayCommodityMoney().replace(".",""));
                log.info("订单【"+out_trade_no+"】 - 应收金额：{}",m);
                int m2 = Integer.valueOf(total_fee);
                log.info("订单【"+out_trade_no+"】 - 实收金额：{}",m2);
                pay.setPayReceipts(""+m2);
                pay.setPayTypeRate("0.06%");

//                //如果支付金额不等于订单金额返回错误
//                if(m2 - m2 * 0.06 >= m){
//                    log.info("订单【"+out_trade_no+"】 - 金额异常，有可能是因为手续费率不一致导致识别错误，请查看手续费是否为0.6%");
//                    return_data.put("return_code", "FAIL");
//                    return_data.put("return_msg", "金额异常");
//                    return XmlUtil.getRequestXml(return_data);
//                }

                log.info("注意！");
                log.info("微信支付手续费不一致，有0.6%-1%，该系统均采用0.6%");
                log.info("如果该条日志出现，则表示微信已经接受到支付，如果有客户反馈，可更加ID"+out_trade_no+"修改相关信息");
                //一切没问题
                //更新支付订单信息
                Order order = orderDao.findByOrderId(pay.getPayOrderId());
                //设置资产
                //创建资产订单
                if (order != null && !"1".equals(order.getOrderCommodityType())){//如果是实物商品，则没有资产
                    Assets assets = new Assets();
                    assets.setAssetsPayId(transaction_id);
                    assets.setAssetsUserId(pay.getPayUserId());
                    Commodity commodity = commodityDao.findByCommodityId(order.getOrderCommodityId());
                    assets.setAssetsNum(order.getOrderNum());
                    assets.setAssetsPhone(pay.getPayPhone());
                    //此处不计算收益
                    assets.setAssetsProfit(new BigDecimal(0));
                    assets.setAssetsFrozenProfit(new BigDecimal(0));
                    assets.setAssetsAvailableProfit(new BigDecimal(0));
                    assets.setAssetsAllProfit(new BigDecimal(0));


                    if (commodity.getCommodityCuring() != -1){
                        Config config = configDao.findById(1).get();
                        assets.setCuring(commodity.getCommodityCuring());
                        assets.setInitialValue(new BigDecimal(order.getOrderMoney()).divide(new BigDecimal(config.getBtcExchange()),16, BigDecimal.ROUND_DOWN));
                        //设置维护费缴纳模式为扣除
                        order.setMaintainPayType(0);
                    }

                    //维护费缴纳模式
                    assets.setMaintainPayType(order.getMaintainPayType());
                    if (1 == order.getMaintainPayType()){//如果是预缴
                        //查询维护费订单
                        List<Maintain> maintains = maintainDao.findByAssetsId(transaction_id);
                        Integer day = 0;
                        for (Maintain maintain:maintains){
                            maintain.setState(0);
                            maintain.setTime(new Date());
                            //计算已经缴纳的维护费
                            if (maintain.getState() == 0){
                                day = day + maintain.getTerm();
                            }
                            maintain.setAssetsId(assets.getAssetsPayId());
                            maintainDao.save(maintain);
                        }
                        assets.setMaintainDay(day);
                    }else if (0 == order.getMaintainPayType()){
                        assets.setMaintainDay(-1);
                    }

                    assets.setAssetsTerm(order.getOrderTerm());
                    assets.setAssetsType(order.getOrderCommodityType());
                    //资产扣除费用初始值0
                    assets.setDeductions("0");

                    //功率
                    assets.setWatt(commodity.getCommodityWatt());
                    assets.setPowerRate(commodity.getCommodityPowerRate());

                    assets.setAssetsName(order.getOrderCommodityName());
                    //计算用户持有当前资产的天数初始值0
                    assets.setAssetsDay("0");
                    //时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if ("0".equals(commodity.getCommodityTime())) {
                        assets.setAssetsTime(new Date());
                    } else {
                        assets.setAssetsTime(new Date(sdf.parse(commodity.getCommodityTime()).getTime()));
                    }
                    //改变资产状态
                    assets.setAssetsState("4");
                    //改变订单状态0
                    order.setOrderState(""+CodeConstant.SUCCESS);
                    order.setPayStart(CodeConstant.SUCCESS);
                    //改变支付订单状态0
                    pay.setPayState(""+CodeConstant.SUCCESS);
                    pay.setVoucherState(""+CodeConstant.SUCCESS);
                    order.setPayId(pay.getPayId());
                    //全部写入
                    assetsDao.save(assets);
                    orderDao.save(order);
                    payDao.save(pay);
                    return_data.put("return_code", "SUCCESS");
                    return_data.put("return_msg", "OK");
                }else if (pay.getPayOrderId().startsWith("MAINTAIN")){
                  Optional<Maintain> maintainOptional = maintainDao.findByMaintainId(pay.getPayOrderId());
                  if (!maintainOptional.isPresent()){
                      log.info("支付校验查询维护费出错");
                      return_data.put("return_code", "FAIL");
                      return_data.put("return_msg", "客户系统异常");
                  }
                  Maintain maintain = maintainOptional.get();
                    Assets assets = assetsDao.findByAssetsPayId(maintain.getAssetsId());
                    //将缴纳的维护费添加到资产
                    //查看当前订单是否有提现
                    List<Reflect> reflectList = reflectDao.findByAssetsId(assets.getAssetsPayId());
                    BigDecimal i = new BigDecimal(0);
                    for (Reflect reflect:reflectList) {
                        if ("1".equals(reflect.getState()) || "3".equals(reflect.getState())){
                            //获取冻结数量
                            i = i.add(reflect.getNum());
                        }
                    }
                    //解除冻结
                    assets.setAssetsProfit((assets.getAssetsFrozenProfit().subtract(i)).add(assets.getAssetsProfit()));
                    assets.setAssetsFrozenProfit(assets.getAssetsFrozenProfit().subtract(i));
                    //

                    Integer day = assets.getMaintainDay();
                    assets.setMaintainDay(day + maintain.getTerm());
                    //判断是否缴清
                    if (assets.getMaintainDay() - Integer.valueOf(assets.getAssetsDay()) >= 0) {
                        assets.setAssetsState("0");
                    }else {
                        assets.setAssetsState("1");
                    }
                    assets.setRemark("");
                  //更改维护费状态
                    maintain.setState(CodeConstant.SUCCESS);
                    //改变支付订单状态0
                    pay.setPayState(""+CodeConstant.SUCCESS);
                    pay.setVoucherState(""+CodeConstant.SUCCESS);
                    maintainDao.save(maintain);
                    payDao.save(pay);
                    return_data.put("return_code", "SUCCESS");
                    return_data.put("return_msg", "OK");
                }else {
                    assert order != null;
                    order.setOrderState(""+CodeConstant.UNSHIPPED);
                    order.setPayStart(CodeConstant.SUCCESS);
                    //改变支付订单状态0
                    pay.setPayState(""+CodeConstant.SUCCESS);
                    pay.setVoucherState(""+CodeConstant.SUCCESS);
                    order.setExpressNum(MessageUtils.get("order.consignment"));
                    order.setPayId(pay.getPayId());
                    orderDao.save(order);
                    payDao.save(pay);
                    return_data.put("return_code", "SUCCESS");
                    return_data.put("return_msg", "OK");
                }
                notice.payNotice("微信收款到账",""+m2,1);
            }
        }else{
            notice.payNotice("微信","签名校验失败",0);
            return_data.put("return_code", "FAIL");
            return_data.put("return_msg", "签名错误");
        }
        return XmlUtil.getRequestXml(return_data);
    }

    //支付宝回调接受
    public String AlipayExamine(HttpServletRequest request) throws AlipayApiException, ParseException {
        log.info("以下是支付宝支付处理日志");
        Bank bank = bankDao.findAllByPayType("Alipay");
        //转参数
        Map<String, String> params = AlipayUtil.convertRequestParamsToMap(request);
        System.out.println();
        //校验签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, bank.getSpare(),
                "UTF-8", "RSA2");
        if (signVerified) {
            if ("TRADE_SUCCESS".equals(params.get("trade_status"))){//交易成功
                Pay pay = payDao.findByPayOrderId(params.get("out_trade_no"));
                if (pay == null) {
                    log.info("支付订单不存在");
                    return "支付订单不存在";
                }
                if ("0".equals(pay.getPayState())) {
                    log.info("订单已经支付");
                    return "订单已经支付";
                }
                double m = Double.valueOf(params.get("total_amount"));
                double m2 = Double.valueOf(pay.getPayCommodityMoney());

                log.info("支付订单-"+params.get("out_trade_no")+"  支付宝实际到账金额：{}",m);
                pay.setPayReceipts(""+m);
                log.info("支付订单-"+params.get("out_trade_no")+"  应收金额：{}",m2);
                log.info("当前支付宝手续费率：0.6%");
                log.info("支付订单-"+params.get("out_trade_no")+"  手续费率：{}",m2*0.006);
//                if (m >= m2 - m2 * 0.006){
//                    return "订单金额异常";
//                }
                log.info("如果该条日志出现，则表示支付宝已经接受到支付，如果有客户反馈，可根据ID【"+params.get("out_trade_no")+"】修改相关信息");
                //一切没问题
                Order order = orderDao.findByOrderId(pay.getPayOrderId());
                if (order != null && !"1".equals(order.getOrderCommodityType())){
                    Assets assets = new Assets();
                    assets.setAssetsPayId(params.get("trade_no"));
                    assets.setAssetsUserId(pay.getPayUserId());
                    Commodity commodity = commodityDao.findByCommodityId(order.getOrderCommodityId());
                    assets.setAssetsNum(order.getOrderNum());
                    assets.setAssetsPhone(pay.getPayPhone());
                    //此处不计算收益
                    assets.setAssetsProfit(new BigDecimal(0));
                    assets.setAssetsFrozenProfit(new BigDecimal(0));
                    assets.setAssetsAvailableProfit(new BigDecimal(0));
                    assets.setAssetsAllProfit(new BigDecimal(0));


                    if (commodity.getCommodityCuring() != -1){
                        Config config = configDao.findById(1).get();
                        assets.setCuring(commodity.getCommodityCuring());
                        assets.setInitialValue(new BigDecimal(order.getOrderMoney()).divide(new BigDecimal(config.getBtcExchange()),16, BigDecimal.ROUND_DOWN));
                        //设置维护费缴纳模式为扣除
                        order.setMaintainPayType(0);
                    }


                    //维护费缴纳模式
                    assets.setMaintainPayType(order.getMaintainPayType());
                    if (1 == order.getMaintainPayType()){//如果是预缴
                        //查询维护费订单
                        List<Maintain> maintains = maintainDao.findByAssetsId(params.get("trade_no"));
                        Integer day = 0;
                        for (Maintain maintain:maintains){
                            maintain.setState(0);
                            maintain.setTime(new Date());
                            //计算已经缴纳的维护费
                            if (maintain.getState() == 0){
                                day = day + maintain.getTerm();
                            }
                            maintain.setAssetsId(assets.getAssetsPayId());
                            maintainDao.save(maintain);
                        }
                        assets.setMaintainDay(day);
                    }else if (0 == order.getMaintainPayType()){
                        assets.setMaintainDay(-1);
                    }

                    assets.setAssetsTerm(order.getOrderTerm());
                    assets.setAssetsType(order.getOrderCommodityType());
                    //资产扣除费用初始值0
                    assets.setDeductions("0");

                    //功率
                    assets.setWatt(commodity.getCommodityWatt());
                    assets.setPowerRate(commodity.getCommodityPowerRate());

                    assets.setAssetsName(order.getOrderCommodityName());
                    //计算用户持有当前资产的天数初始值0
                    assets.setAssetsDay("0");

                    //时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if ("0".equals(commodity.getCommodityTime())) {
                        assets.setAssetsTime(new Date());
                    } else {
                        assets.setAssetsTime(new Date(sdf.parse(commodity.getCommodityTime()).getTime()));
                    }
                    //改变资产状态
                    assets.setAssetsState("4");
                    //改变订单状态0
                    order.setOrderState(""+CodeConstant.SUCCESS);
                    order.setPayStart(CodeConstant.SUCCESS);
                    //改变支付订单状态0
                    pay.setPayTypeId(params.get("trade_no"));
                    pay.setUserId("Alipay");
                    pay.setPayState(""+CodeConstant.SUCCESS);
                    pay.setVoucherState(""+CodeConstant.SUCCESS);
                    order.setPayId(pay.getPayId());
                    //全部写入
                    assetsDao.save(assets);
                    orderDao.save(order);
                    payDao.save(pay);
                    log.info("支付完成");
                    notice.payNotice("支付宝到账",""+m2,1);
                    return "success";
                }else if (pay.getPayOrderId().startsWith("MN")){
                    Optional<Maintain> maintainOptional = maintainDao.findByMaintainId(pay.getPayOrderId());
                    if (!maintainOptional.isPresent()){
                        log.info("支付校验查询维护费出错");
                        return "支付校验查询维护费出错";
                    }
                    Maintain maintain = maintainOptional.get();
                    //将缴纳的维护费添加到资产
                    Assets assets = assetsDao.findByAssetsPayId(maintain.getAssetsId());
                    /////////////////////////////////////////////
                    List<Reflect> reflectList = reflectDao.findByAssetsId(assets.getAssetsPayId());
                    BigDecimal i = new BigDecimal(0);
                    for (Reflect reflect:reflectList) {
                        if ("1".equals(reflect.getState()) || "3".equals(reflect.getState())){
                            //获取冻结数量
                            i = i.add(reflect.getNum());
                        }
                    }
                    //解除冻结
                    assets.setAssetsProfit((assets.getAssetsFrozenProfit().subtract(i)).add(assets.getAssetsProfit()));
                    assets.setAssetsFrozenProfit(assets.getAssetsFrozenProfit().subtract(i));
                    //
                    Integer day = assets.getMaintainDay();
                    assets.setMaintainDay(day + maintain.getTerm());
                    assets.setAssetsState("0");
                    assets.setRemark("");
                    //更改维护费状态
                    maintain.setState(CodeConstant.SUCCESS);
                    pay.setPayTypeId(params.get("trade_no"));
                    pay.setUserId("Alipay");
                    pay.setPayState(""+CodeConstant.SUCCESS);
                    pay.setVoucherState(""+CodeConstant.SUCCESS);
                    maintainDao.save(maintain);
                    payDao.save(pay);
                    log.info("支付完成");
                    notice.payNotice("支付宝到账",""+m2,1);
                    return "success";
                }else {
                    assert order != null;
                    order.setOrderState(""+CodeConstant.UNSHIPPED);
                    //改变支付订单状态0
                    pay.setPayTypeId(params.get("trade_no"));
                    pay.setUserId("Alipay");
                    pay.setPayState(""+CodeConstant.SUCCESS);
                    pay.setVoucherState(""+CodeConstant.SUCCESS);
                    order.setExpressNum(MessageUtils.get("order.consignment"));
                    order.setPayId(pay.getPayId());
                    orderDao.save(order);
                    payDao.save(pay);
                    notice.payNotice("支付宝到账",""+m2,1);
                    log.info("支付完成");
                   return "success";
                }
            }else {
                log.info("交易失败");
                notice.payNotice("支付宝",params.get("out_trade_no"),0);
                return "failure";
            }
        }else {
            //签名校验失败
            log.info("签名校验失败");
            notice.payNotice("支付宝","签名校验失败",0);
            return "failure";
        }
    }

    /**
     * 转账支付，微信支付，支付宝支付
     * 财务审核方法,仅审核云算力套餐和实物商品
     * @param id 审核的支付订单
     * @param  q 审核通过与否
     * @param payReceipts 实际收款金额
     * @param payTypeId 转账单号
     * @return
     */
    @RequestLimit(count = 1,time = 500)
    @Transactional(rollbackFor = Exception.class)
    public Json Examine(String id,String q,String payReceipts,String payTypeId,String remarks) throws UnsupportedEncodingException {
        String userId = (String)SecurityUtils.getSubject().getPrincipal();
        boolean o = false;
        if (q != null && !"".equals(q)){
            o = "0".equals(q);
        }
        if (!Regular.isSql(payReceipts)){
            return API.error("金额有误");
        }
        if (!Regular.isSql(payTypeId)){
            return API.error("id有误");
        }
        Optional<Pay> optionalPay = payDao.findByPayId(id);
        //查询支付订单
        if (!optionalPay.isPresent()) {
            log.warn("审核办法，支付订单【"+id+"】不存在");
            return API.error(MessageUtils.get("assets.examine.nonentity"));
        }
        Pay pay = optionalPay.get();
        if ((""+CodeConstant.SUCCESS).equals(pay.getPayState())) {
            log.warn("审核办法，ID【"+id+"】已经审核过了");
            return API.error(MessageUtils.get("assets.examine.pass"));
        }
        if (id == null || id.equals(""))
            return API.error(MessageUtils.get("currency.id.empty"));
        //查询商品订单
        if (!pay.getPayOrderId().startsWith("MN")) {
            Order order = orderDao.findByOrderId(pay.getPayOrderId());
            if (order == null) {
                log.warn("审核办法，商品订单【" + pay.getPayOrderId() + "】不存在");
                return API.error(MessageUtils.get("assets.examine.order.existent"));
            }
            //查询商品
            Commodity commodity = commodityDao.findByCommodityId(order.getOrderCommodityId());
            if (commodity == null) {
                log.warn("审核办法，商品不存在【" + order.getOrderCommodityId() + "】");
                return API.error(MessageUtils.get("assets.examine.commodity.existent"));
            }
            try {
                if (o) {
                    //创建资产订单
                    Assets assets = new Assets();
                    //写入支付信息
                    if (payReceipts == null || "".equals(payReceipts))
                        pay.setPayReceipts(order.getOrderMoney());
                    else
                        pay.setPayReceipts(payReceipts);
                    pay.setPayTypeRate("0");
                    if (payTypeId == null || "".equals(payTypeId))
                        pay.setPayTypeId("审核者未提供转账id");
                    else
                        pay.setPayTypeId(payTypeId);
                    pay.setUserId(userId);
                    pay.setExamineTime(new Date());
                    if (!"1".equals(order.getOrderCommodityType())) {
                        //写入资产
                        assets.setAssetsPayId(id);
                        assets.setAssetsUserId(pay.getPayUserId());
                        assets.setAssetsNum(order.getOrderNum());
                        if (pay.getPayPhone() != null && !"".equals(pay.getPayPhone()))
                            assets.setAssetsPhone(pay.getPayPhone());
                        else if (pay.getPayEmail() != null && !"".equals(pay.getPayEmail()))
                            assets.setAssetsPhone(pay.getPayEmail());
                        //此处不计算收益
                        assets.setAssetsProfit(new BigDecimal(0));
                        assets.setAssetsFrozenProfit(new BigDecimal(0));
                        assets.setAssetsAvailableProfit(new BigDecimal(0));
                        assets.setAssetsAllProfit(new BigDecimal(0));

                        assets.setAssetsType(order.getOrderCommodityType());

                        if (commodity.getCommodityCuring() != -1){
                            Config config = configDao.findById(1).get();
                            assets.setCuring(commodity.getCommodityCuring());
                            assets.setInitialValue(new BigDecimal(order.getOrderMoney()).divide(new BigDecimal(config.getBtcExchange()),16, BigDecimal.ROUND_DOWN));
                            //设置维护费缴纳模式为扣除
                            order.setMaintainPayType(0);
                        }

                        //维护费缴纳模式
                        assets.setMaintainPayType(order.getMaintainPayType());
                        if (1 == order.getMaintainPayType()) {//如果是预缴
                            //查询维护费订单
                            List<Maintain> maintains = maintainDao.findByAssetsId(order.getOrderId());
                            Integer day = 0;
                            for (Maintain maintain : maintains) {
                                maintain.setState(0);
                                maintain.setTime(new Date());
                                //计算已经缴纳的维护费
                                day = day + maintain.getTerm();
                                maintain.setAssetsId(assets.getAssetsPayId());
                                maintainDao.save(maintain);
                            }
                            assets.setMaintainDay(day);
                        } else if (0 == order.getMaintainPayType()) {
                            assets.setMaintainDay(-1);
                        }

                        assets.setAssetsTerm(order.getOrderTerm());
                        //资产扣除费用初始值0
                        assets.setDeductions("0");

                        //功率
                        assets.setWatt(commodity.getCommodityWatt());
                        assets.setPowerRate(commodity.getCommodityPowerRate());

                        assets.setAssetsName(order.getOrderCommodityName());
                        //计算用户持有当前资产的天数初始值0
                        assets.setAssetsDay("0");
                        //时间
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        if ("0".equals(commodity.getCommodityTime())) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            calendar.add(Calendar.DATE,1);
                            assets.setAssetsTime(calendar.getTime());
                        } else {
                            assets.setAssetsTime(new Date(sdf.parse(commodity.getCommodityTime()).getTime()));
                        }
                        //改变资产状态
                        assets.setAssetsState("4");
                        //改变订单状态0
                        order.setOrderState("" + CodeConstant.SUCCESS);
                        order.setPayStart(CodeConstant.SUCCESS);
                        order.setPayId(assets.getAssetsPayId());
                        //改变支付订单状态0
                        pay.setPayState("" + CodeConstant.SUCCESS);
                        pay.setVoucherState("" + CodeConstant.SUCCESS);
                        //全部写入
                        assetsDao.save(assets);
                        orderDao.save(order);
                        payDao.save(pay);
                        notice.smsPay(pay.getPayPhone(),"已通过","请前往平台进行确认！");
                        return API.Success(pay);
                    } else {
                        //改变订单状态0
                        order.setOrderState("" + CodeConstant.UNSHIPPED);
                        order.setPayStart(CodeConstant.SUCCESS);
                        //改变支付订单状态0
                        pay.setPayState("" + CodeConstant.SUCCESS);
                        pay.setVoucherState("" + CodeConstant.SUCCESS);
                        order.setExpressNum(MessageUtils.get("order.consignment"));
                        order.setPayId(assets.getAssetsPayId());
                        //全部写入
                        orderDao.save(order);
                        payDao.save(pay);
                        notice.smsPay(pay.getPayPhone(),"已通过","请前往平台进行确认！");
                        return API.Success(order);
                    }
                } else {
                    //改变支付订单状态
                    pay.setPayState("" + CodeConstant.NOT_PASS);
                    order.setOrderState("" + CodeConstant.PAYMENT);
                    payDao.save(pay);
                    notice.smsPay(pay.getPayPhone(),"已拒绝","原因："+remarks);
                    return API.Success(pay);
                }
            } catch (Exception e) {
                log.warn("审核方法，错误的布尔值！！");
                return API.error(MessageUtils.get("currency.parameter.error"));
            }
        }else {
                Optional<Maintain> optionalMaintain = maintainDao.findByMaintainId(pay.getPayOrderId());
                if (!optionalMaintain.isPresent())
                    return API.error("审核的维护费订单不存在");
                if (payTypeId == null || "".equals(payTypeId))
                    pay.setPayTypeId("审核者未提供转账id");
                else
                    pay.setPayTypeId(payTypeId);
                pay.setUserId(userId);
                Maintain maintain = optionalMaintain.get();
            if (o){
                if (payReceipts == null || "".equals(payReceipts))
                    pay.setPayReceipts(String.valueOf(maintain.getMoney()));
                else
                    pay.setPayReceipts(payReceipts);
                pay.setPayTypeRate("0");
                //将缴纳的维护费添加到资产
                Assets assets = assetsDao.findByAssetsPayId(maintain.getAssetsId());
                /////////////////////////////////////////////
                List<Reflect> reflectList = reflectDao.findByAssetsId(assets.getAssetsPayId());
                BigDecimal i = new BigDecimal(0);
                for (Reflect reflect:reflectList) {
                    if ("1".equals(reflect.getState()) || "3".equals(reflect.getState())){
                        //获取冻结数量
                        i = i.add(reflect.getNum());
                    }
                }
                //解除冻结
                assets.setAssetsProfit((assets.getAssetsFrozenProfit().subtract(i)).add(assets.getAssetsProfit()));
                assets.setAssetsFrozenProfit(i);
                //
                Integer day = assets.getMaintainDay();
                assets.setMaintainDay(day + maintain.getTerm());
                assets.setAssetsState("0");
                assets.setRemark("");
                maintain.setState(CodeConstant.SUCCESS);
                pay.setPayState(""+CodeConstant.SUCCESS);
                pay.setVoucherState(""+CodeConstant.SUCCESS);
                maintainDao.save(maintain);
                payDao.save(pay);
                notice.smsPay(pay.getPayPhone(),"已通过","请前往平台进行确认！");
                return API.Success(maintain);
            }else {
                maintain.setState(CodeConstant.PAYMENT);
                notice.smsPay(pay.getPayPhone(),"已拒绝","原因："+remarks);
                return API.Success("拒绝");
            }
        }
    }

    /**
     * 全部资产查询
     * @return
     */
    public Json AssetsAll(Integer index,Integer size, String id){
        if (index == null ){
            index = 0;
        }else {
            index = index - 1;
        }
        if (size == null || 0 == size){
            size = 10;
        }
        if (!Regular.isSql(id)){
            return API.error("id有误");
        }
        Pageable pageable = PageRequest.of(index,size);
        List<Assets> list = new ArrayList<>();
        Page<Assets> assetsPage = null;
        if (id != null && !"".equals(id)){
            assetsPage = assetsDao.findByAssetsPayId(id,pageable);
        }else {
            assetsPage = assetsDao.findAll(pageable);
        }
        for (Assets a:assetsPage) {
            list.add(a);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("size",assetsDao.count());
        map.put("data",list);
        return API.Success(map);
    }


    /**
     * 单个查询
     * @param id
     * @return
     */
    public Json AssetsOne(Integer id){
        if (id == null)
            return API.error(MessageUtils.get("currency.id.empty"));
        Optional<Assets> assets = assetsDao.findById(id);
        return API.Success(assets.orElse(null));
    }


    /**
     * 修改资产
     * @param assets
     * @return
     */
    public Json AssetsUpdate(Assets assets){
        if (!Regular.isEntity(assets))
            return API.error("参数不正确");
        Assets assets1 = assetsDao.findById(assets.getId()).orElse(null);
        if (assets1 != null) {
            assets1.setAssetsTime(assets.getAssetsTime());
            assets1.setAssetsState(assets.getAssetsState());
            assets1.setAssetsDay(assets.getAssetsDay());
            assets1.setAssetsName(assets.getAssetsName());
            assets1.setDeductions(assets.getDeductions());
            assets1.setAssetsType(assets.getAssetsType());
            assets1.setAssetsTerm(assets.getAssetsTerm());
            assets1.setAssetsProfit(assets.getAssetsProfit());
            assets1.setAssetsPhone(assets.getAssetsPhone());
            assets1.setAssetsNum(assets.getAssetsNum());
        }else {
            return API.error("资产不存在");
        }
        assetsDao.save(assets1);
        return API.Success(assets1);
    }


    /**
     * 手动补齐用户欠缺的资产
     * @return
     * @throws ParseException
     */
    public Json sdfs(String userId) throws ParseException {
        List<Assets> assetsList = assetsDao.findByAssetsUserId(userId);
        if (assetsList == null)
            return API.error("用户没有资产");
        Config config = configDao.findById(1).get();
        Date date2 = new Date();
        for (int i = 0;i < assetsList.size() - 1;i++) {
            for (int j = 0; j < assetsList.size() - 1 - i; j++) {
                if (assetsList.get(j).getAssetsTime().getTime() < assetsList.get(j+1).getAssetsTime().getTime())
                    date2 = assetsList.get(j).getAssetsTime();
                else
                    date2 = assetsList.get(j+1).getAssetsTime();
            }
        }
            Calendar c = Calendar.getInstance();
            DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = dateFormat1.parse(dateFormat1.format(new Date()));
            Date date = dateFormat1.parse(dateFormat1.format(date2));
            while (!date.equals(date1)) {
                c.setTime(date);
                c.add(Calendar.DATE, 1);
                date = c.getTime();
                BigDecimal BTCPowerRate = new BigDecimal(0);
                BigDecimal profit = new BigDecimal(0);
                /////////////////////补齐每日每单///////////////////
                for (Assets assets:assetsList) {
                        if ("0".equals(assets.getAssetsState()) || "1".equals(assets.getAssetsState()) || "4".equals(assets.getAssetsState())) {
                            if (Time.belongDateDay(date, assets.getAssetsTime(), 0)) {
                                if (!Time.belongDateDay(date, assets.getAssetsTime(), Integer.valueOf(assets.getAssetsTerm()) + 1)) {


                                    if (assets.getCuring() == null || assets.getCuring() == -1) {

                                        if (assetsDayDao.findByUserIdAndTimeAndAssetsId(userId,date,assets.getAssetsPayId()) == null) {
                                            AssetsDay assetsDay = new AssetsDay();
                                            assetsDay.setUserId(assets.getAssetsUserId());
                                            assetsDay.setAssetsId(assets.getAssetsPayId());
                                            assetsDay.setTerm(Integer.valueOf(assets.getAssetsTerm()));
                                            //
                                            assetsDay.setKuangchi("BTC.com");

                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTime(date);
                                            calendar.add(Calendar.DATE, -1);
                                            assetsDay.setTime(calendar.getTime());

                                            //计算可维护天数
                                            assetsDay.setValidMaintainDay(Integer.valueOf(assets.getAssetsDay()) - assets.getMaintainDay());

                                            //计算当天收益和电费

                                            BigDecimal num = new BigDecimal(Integer.valueOf(assets.getAssetsNum()));//获取资产数量
                                            BigDecimal decimal = config.getCoin();//获取理论收益
                                            BigDecimal unit = num.multiply(decimal);

                                            //收益
                                            assetsDay.setProfit(unit.subtract(assets.getAssetsFrozenProfit()).subtract(assets.getAssetsAvailableProfit()).setScale(16, BigDecimal.ROUND_DOWN));

                                            //计算电费
                                            Double powerRate = assets.getPowerRate() * (assets.getWatt() / 1000) * Double.valueOf(assets.getAssetsNum()) * 24;//一天的电费
                                            //换算成当前币值
                                            BigDecimal BTCPowerRates = new BigDecimal(powerRate / config.getBtcExchange());//模拟值

                                            assetsDay.setPowerRate(BTCPowerRates.setScale(16, BigDecimal.ROUND_DOWN));

                                            assetsDayDao.save(assetsDay);
                                        }

                                        if (assetsDateDao.findByUserIdAndTime(userId,date) == null) {
                                            /////////////////////////补齐每日收益///////////////////////////
                                            BigDecimal num = new BigDecimal(Integer.valueOf(assets.getAssetsNum()));//获取资产数量
                                            BigDecimal decimal = config.getCoin();//获取理论收益
                                            BigDecimal unit = num.multiply(decimal);
                                            Double powerRate = assets.getPowerRate() * (assets.getWatt() / 1000) * Double.valueOf(assets.getAssetsNum()) * 24;//一天的电费
                                            if (assets.getMaintainPayType() == 0) {//扣除模式
                                                if (BTCPowerRate.compareTo(unit) < 0) {//如果小于收益
                                                    profit = profit.add(unit);//所有资产单天收益
                                                    //换算成当前币值
                                                    BTCPowerRate = BTCPowerRate.add(new BigDecimal(powerRate / config.getBtcExchange()));
                                                } else {
                                                    profit = profit.add(new BigDecimal(0));
                                                    BTCPowerRate = BTCPowerRate.add(new BigDecimal(0));
                                                }
                                            } else if (assets.getMaintainPayType() == 1) {//预缴模式
                                                profit = profit.add(unit);//所有资产单天收益
                                                //换算成当前币值
                                                BTCPowerRate = BTCPowerRate.add(new BigDecimal(powerRate / config.getBtcExchange()));//模拟值
                                            }
                                        }
                                    }
                                }
                            }
                        }

                }

                if (profit.compareTo(new BigDecimal(0)) != 0) {//当日累计产出等于0则不计算
                    AssetsDate assetsDate = new AssetsDate();
                    assetsDate.setKuangchi("BTC.com");
                    assetsDate.setUserId(userId);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DATE, -1);
                    assetsDate.setTime(calendar.getTime());
                    //
                    assetsDate.setProfit(profit.setScale(16, BigDecimal.ROUND_DOWN));
                    assetsDate.setPowerRate(BTCPowerRate.setScale(16, BigDecimal.ROUND_DOWN));
                    assetsDateDao.save(assetsDate);
                }


            }
        return API.Success();
    }
}
