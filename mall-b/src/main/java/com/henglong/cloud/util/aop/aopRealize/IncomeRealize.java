package com.henglong.cloud.util.aop.aopRealize;

import com.henglong.cloud.dao.*;
import com.henglong.cloud.dao.other.AssetsDayDao;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Config;
import com.henglong.cloud.entity.Reflect;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.entity.other.AssetsDay;
import com.henglong.cloud.util.Time;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Component
@Aspect
public class IncomeRealize {

    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(IncomeRealize.class);

    private final UserDao userDao;

    private final AssetsDao assetsDao;

    private final ConfigDao configDao;

    private final ReflectDao reflectDao;

    private final AssetsDayDao assetsDayDao;

    @Autowired
    public IncomeRealize(AssetsDao assetsDao, UserDao userDao, ConfigDao configDao, ReflectDao reflectDao, AssetsDayDao assetsDayDao) {
        this.assetsDao = assetsDao;
        this.userDao = userDao;
        this.configDao = configDao;
        this.reflectDao = reflectDao;
        this.assetsDayDao = assetsDayDao;
    }


    @Pointcut("@annotation(com.henglong.cloud.util.aop.aopName.Income)")
    public void Income() {
    }


    /**
     * 计算用户收益，注解在用户个人信息获取接口
     */
    @After("Income()")
    public void Inc() {
        //获取用户信息
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        if (user == null)
            return;
        //获取配置文件
        Optional<Config> configOptional = configDao.findById(1);
        Config config = configOptional.get();
        //获取用户资产信息
        List<Assets> assetsList = assetsDao.findByAssetsUserId(userId);
        if (assetsList == null)
            return;
        for (Assets assets : assetsList) {
            //判断币类型
            if ("BTC".equals(assets.getAssetsType())) {
                //判断当前资产是否有期限
                if ("0".equals(assets.getAssetsTerm())) {
                    //非期限商品
                    //判断是否到达开始时间
                    if (Time.belongDateDay(new Date(), assets.getAssetsTime(), 0)) {//永久商品
                        if ("0".equals(assets.getAssetsState())) {//判断资产状态是否正常
                            Integer day = Time.differentDays(assets.getAssetsTime(), new Date());
                            //计算天数
                            if (day >= 0 && (day + 1) <= Integer.valueOf(assets.getAssetsTerm()))
                                assets.setAssetsDay("" + day);

                            if (assets.getCuring() != -1) {
                                if (!"0".equals(assets.getAssetsTerm()) && day.equals(Integer.valueOf(assets.getAssetsTerm()))) {//计算固化
                                    //计算收益
                                    assets.setAssetsAllProfit(assets.getInitialValue().multiply(new BigDecimal(assets.getCuring())));
                                    assets.setAssetsProfit(assets.getInitialValue().multiply(new BigDecimal(assets.getCuring())));
                                    assets.setAssetsState("2");
                                    assets.setRemark("资产已经发放了所有收益");
                                }
                            } else {//非固化

                                if (0 == assets.getMaintainPayType()) {//不预交

                                    BigDecimal num = new BigDecimal(Integer.valueOf(assets.getAssetsNum()));//获取资产数量
                                    BigDecimal decimal = config.getCoin();//获取理论收益
                                    ////////////////之前的计算方法，精确到分钟
//                                BigDecimal unit = ((num.multiply(decimal)).divide(new BigDecimal(24)))
//                                        .subtract(new BigDecimal(String.valueOf(Result.result(assets.getWatt() + "/100"))).multiply(num));
//                                BigDecimal profit = (unit.multiply(new BigDecimal(Time.diffHours(new Date(), assets.getAssetsTime()))));
//                                assets.setDeductions("" + (new BigDecimal(String.valueOf(Result.result(assets.getWatt() + "/100"))).multiply(num)).multiply(new BigDecimal(Time.diffHours(new Date(), assets.getAssetsTime()))));
                                    //////////////////////////////////////////////
                                    //新的计算方法
                                    BigDecimal unit = num.multiply(decimal);//单位日收益,数量*理论单位收益
                                    BigDecimal profit = unit.multiply(new BigDecimal(Time.differentDays(assets.getAssetsTime(), new Date())));//资产收益，单位收益*持有天数
                                    //计算电费
                                    Double powerRate = assets.getPowerRate() * (assets.getWatt() / 1000) * Double.valueOf(assets.getAssetsNum()) * 24;//一天的电费
                                    Double totalPowerRate = powerRate * day;
                                    //////////新的计算模式，采用每日每单进行计算//////////////////
                                    List<AssetsDay> assetsDayList = assetsDayDao.findByAssetsId(assets.getAssetsPayId());
                                    BigDecimal z = new BigDecimal(0);
                                    BigDecimal zd = new BigDecimal(0);
                                    for (AssetsDay assetsDay : assetsDayList) {
                                        if (assetsDay.getPowerRate().compareTo(assetsDay.getProfit()) < 0) {
                                            z = z.add(assetsDay.getProfit().subtract(assetsDay.getPowerRate()));
                                        } else {
                                            z = z.add(new BigDecimal(0));
                                        }
                                        zd = zd.add(assetsDay.getPowerRate());
                                    }
                                    //查验当前是否有在提现中的订单
                                    List<Reflect> reflectList = reflectDao.findByAssetsId(assets.getAssetsPayId());
                                    BigDecimal reNum = new BigDecimal(0).setScale(16, BigDecimal.ROUND_DOWN);
                                    for (Reflect reflect : reflectList) {
                                        if (!"0".equals(reflect.getState()))
                                            reNum = reNum.add(reflect.getNum());
                                    }
                                    //换算成当前币值
                                    BigDecimal BTCPowerRate = new BigDecimal(totalPowerRate / config.getBtcExchange());//模拟值
                                    assets.setDeductions("" + zd.setScale(16, BigDecimal.ROUND_DOWN));
//                                    assets.setAssetsProfit(((profit.subtract(BTCPowerRate)).subtract(assets.getAssetsAvailableProfit()).subtract(reNum)).setScale(16, BigDecimal.ROUND_DOWN));
                                    assets.setAssetsProfit(z.subtract(reNum).subtract(assets.getAssetsAvailableProfit()).setScale(16, BigDecimal.ROUND_DOWN));
//                                    assets.setAssetsAllProfit(profit.subtract(BTCPowerRate).setScale(16, BigDecimal.ROUND_DOWN));
                                    assets.setAssetsAllProfit(z.add(assets.getAssetsAvailableProfit()).add(assets.getAssetsFrozenProfit()).setScale(16, BigDecimal.ROUND_DOWN));
                                    assets.setAssetsState("0");
                                } else if (1 == assets.getMaintainPayType()) {//预交
                                    if (assets.getMaintainDay() - day < 0) {
                                        BigDecimal num = new BigDecimal(Integer.valueOf(assets.getAssetsNum()));//获取资产数量
                                        BigDecimal decimal = config.getCoin();//获取理论收益

                                        //查验当前是否有在提现中的订单
                                        List<Reflect> reflectList = reflectDao.findByAssetsId(assets.getAssetsPayId());
                                        BigDecimal reNum = new BigDecimal(0).setScale(16, BigDecimal.ROUND_DOWN);
                                        for (Reflect reflect : reflectList) {
                                            if (!"0".equals(reflect.getState()) && !"2".equals(reflect.getState()))
                                                reNum = reNum.add(reflect.getNum());
                                        }
                                        BigDecimal unit = num.multiply(decimal);//单位日收益,数量*理论单位收益
                                        BigDecimal profit = unit.multiply(new BigDecimal(Time.differentDays(assets.getAssetsTime(), new Date())));//资产天收益，单位收益*持有天数
                                        assets.setDeductions("预交费");
                                        assets.setAssetsProfit((profit.subtract(assets.getAssetsAvailableProfit()).subtract(reNum)).setScale(16, BigDecimal.ROUND_DOWN));
                                        assets.setAssetsAllProfit(profit.setScale(16, BigDecimal.ROUND_DOWN));
                                    } else {
                                        assets.setAssetsState("1");
                                        assets.setRemark("资产维护费不足");
                                    }
                                } else {
                                    assets.setAssetsState("4");
                                    assets.setRemark("获取资产维护费状态失败，请联系管理员");
                                }
                            }
                        } else if ("2".equals(assets.getAssetsState())) {//资产已经发放了所有收益
                            log.info("第三个");
                            assets.setAssetsState("2");
                            assets.setRemark("资产已经发放了所有收益");
                        } else if ("3".equals(assets.getAssetsState())) {//资产已经被管理员取消
                            assets.setAssetsState("3");
                            assets.setRemark("资产已经被管理员取消");
                        }
                    }
                } else {
                    //期限商品
                    //判断是否到达开始时间
                    if (Time.belongDateDay(new Date(), assets.getAssetsTime(), 0)) {
                        //判断是否在期限时间段内
                        if (!Time.belongDateDay(new Date(), assets.getAssetsTime(), Integer.valueOf(assets.getAssetsTerm()) + 1)) {
                            //判断资产是否正常
                            if ("0".equals(assets.getAssetsState()) || "1".equals(assets.getAssetsState()) || "4".equals(assets.getAssetsState())) {
                                Integer day = Time.differentDays(assets.getAssetsTime(), new Date());
                                //计算天数
                                if (day >= 0 && (day + 1) <= Integer.valueOf(assets.getAssetsTerm()))
                                    assets.setAssetsDay("" + (day + 1));

                                if (assets.getCuring() != null && assets.getCuring() != -1) {
                                    assets.setAssetsState("0");
                                    if (!"0".equals(assets.getAssetsTerm()) && day.equals(Integer.valueOf(assets.getAssetsTerm()))) {//计算固化
                                        log.info("第1个");
                                        //计算收益
                                        assets.setAssetsAllProfit(assets.getInitialValue().multiply(new BigDecimal(assets.getCuring())).add(assets.getInitialValue()).setScale(16, BigDecimal.ROUND_DOWN));
                                        assets.setAssetsProfit(assets.getInitialValue().multiply(new BigDecimal(assets.getCuring())).add(assets.getInitialValue()).setScale(16, BigDecimal.ROUND_DOWN));
                                        assets.setAssetsState("2");
                                        assets.setRemark("资产已经发放了所有收益");
                                    }
                                } else {//非固化
                                    if (0 == assets.getMaintainPayType()) {//不预交

                                        //////////新的计算模式，采用每日每单进行计算//////////////////
                                        List<AssetsDay> assetsDayList = assetsDayDao.findByAssetsId(assets.getAssetsPayId());
                                        BigDecimal z = new BigDecimal(0);
                                        BigDecimal zd = new BigDecimal(0);
                                        if (assetsDayList != null) {
                                            for (AssetsDay assetsDay : assetsDayList) {
                                                if (assetsDay.getPowerRate().compareTo(assetsDay.getProfit()) < 0) {
                                                    z = z.add(assetsDay.getProfit().subtract(assetsDay.getPowerRate()));
                                                } else {
                                                    z = z.add(new BigDecimal(0));
                                                }
                                                zd = zd.add(assetsDay.getPowerRate());
                                            }
                                        }

                                        //查验当前是否有在提现中的订单
//                                            List<Reflect> reflectList = reflectDao.findByAssetsId(assets.getAssetsPayId());
//                                            BigDecimal reNum = new BigDecimal(0).setScale(16, BigDecimal.ROUND_DOWN);
//                                            for (Reflect reflect : reflectList) {
//                                                if (!"0".equals(reflect.getState()))
//                                                    reNum = reNum.add(reflect.getNum());
//                                            }

                                        assets.setDeductions("" + zd.setScale(16, BigDecimal.ROUND_DOWN));

                                        assets.setAssetsProfit(z.subtract(assets.getAssetsAvailableProfit()).subtract(assets.getAssetsFrozenProfit()).setScale(16, BigDecimal.ROUND_DOWN));
                                        assets.setAssetsAllProfit(z);
                                        assets.setAssetsState("0");
                                    } else if (1 == assets.getMaintainPayType()) {//预交
                                        if (assets.getMaintainDay() - day >= 0) {
                                            List<AssetsDay> assetsDayList = assetsDayDao.findByAssetsId(assets.getAssetsPayId());
                                            BigDecimal z = new BigDecimal(0);
                                            BigDecimal zd = new BigDecimal(0);
                                            if (assetsDayList != null) {
                                                for (AssetsDay assetsDay : assetsDayList) {
                                                    z = z.add(assetsDay.getProfit());
                                                    zd = zd.add(assetsDay.getPowerRate());
                                                }
                                            }

                                            //查验当前是否有在提现中的订单
//                                                List<Reflect> reflectList = reflectDao.findByAssetsId(assets.getAssetsPayId());
//                                                BigDecimal reNum = new BigDecimal(0).setScale(16, BigDecimal.ROUND_DOWN);
//                                                for (Reflect reflect : reflectList) {
//                                                    if (!"0".equals(reflect.getState()) && !"2".equals(reflect.getState()))
//                                                        reNum = reNum.add(reflect.getNum());
//                                                }

                                            assets.setDeductions("预交费");
                                            assets.setAssetsProfit(z.subtract(assets.getAssetsAvailableProfit()).subtract(assets.getAssetsFrozenProfit()).setScale(16, BigDecimal.ROUND_DOWN));
                                            assets.setAssetsAllProfit(z);
                                            assets.setAssetsState("0");
                                        } else if (assets.getMaintainDay() - day < 0) {

                                            List<AssetsDay> assetsDayList = assetsDayDao.findByAssetsId(assets.getAssetsPayId());
                                            BigDecimal z = new BigDecimal(0);
                                            BigDecimal zd = new BigDecimal(0);
                                            if (assetsDayList != null) {
                                                for (AssetsDay assetsDay : assetsDayList) {
                                                    z = z.add(assetsDay.getProfit());
                                                    zd = zd.add(assetsDay.getPowerRate());
                                                }
                                            }

                                            //查验当前是否有在提现中的订单
//                                                    List<Reflect> reflectList = reflectDao.findByAssetsId(assets.getAssetsPayId());
//                                                    BigDecimal reNum = new BigDecimal(0).setScale(16, BigDecimal.ROUND_DOWN);
//                                                    for (Reflect reflect : reflectList) {
//                                                        if (!"0".equals(reflect.getState()) && !"2".equals(reflect.getState()))
//                                                            reNum = reNum.add(reflect.getNum());
//                                                    }

                                            assets.setDeductions("预交费");
                                            assets.setAssetsFrozenProfit(z.subtract(assets.getAssetsProfit()).subtract(assets.getAssetsAvailableProfit()).setScale(16, BigDecimal.ROUND_DOWN));
                                            assets.setAssetsAllProfit(z);
                                            assets.setAssetsState("1");
                                            assets.setRemark("资产维护费不足");
                                        }
                                    }
                                }
                            } else if ("2".equals(assets.getAssetsState())) {//资产已经发放了所有收益
                                assets.setAssetsState("2");
                                assets.setRemark("资产已经发放了所有收益");
                            } else if ("3".equals(assets.getAssetsState())) {//资产已经被管理员取消
                                assets.setAssetsState("3");
                                assets.setRemark("资产已经被管理员取消");
                            }
                        } else {//资产已完成
                            assets.setAssetsState("2");
                            assets.setRemark("资产已经发放了所有收益");
                        }
                    }
                    assetsDao.save(assets);
                }//没有ETH，暂时不写
            }
        }
    }
}

