package com.henglong.cloud.service;

import com.google.gson.Gson;
import com.henglong.cloud.dao.AssetsDao;
import com.henglong.cloud.dao.ConfigDao;
import com.henglong.cloud.dao.ReflectDao;
import com.henglong.cloud.dao.UserDao;
import com.henglong.cloud.dao.other.AssetsDayDao;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Config;
import com.henglong.cloud.entity.Reflect;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.entity.other.AssetsDay;
import com.henglong.cloud.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ConfigServer {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(ConfigServer.class);

    private final ConfigDao configDao;

    private final EmailService emailService;

    private final UserDao userDao;

    private final AssetsDao assetsDao;

    private final ReflectDao reflectDao;

    private final AssetsDayDao assetsDayDao;

    @Autowired
    public ConfigServer(ConfigDao configDao, EmailService emailService, UserDao userDao, AssetsDao assetsDao, ReflectDao reflectDao, AssetsDayDao assetsDayDao) {
        this.configDao = configDao;
        this.emailService = emailService;
        this.userDao = userDao;
        this.assetsDao = assetsDao;
        this.reflectDao = reflectDao;
        this.assetsDayDao = assetsDayDao;
    }

    @Scheduled(cron = "0 0 23 * * ?")//每天晚上11点执行
    public void coin(){
        Optional<Config> configOptional = configDao.findById(1);
        if (!configOptional.isPresent())
            return;
        Config config = configOptional.get();
        try {
            URL url = new URL("https://www.f2pool.com/help/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int code = urlConnection.getResponseCode();
            if (code != 200) {
                log.error("打开网络资源失败,状态码为："+code);
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
            String rb, r = "";

            while ((rb = bufferedReader.readLine())!= null){
                r += rb;
            }



            String s = FileConfig.getSubUtilSimple(r,"<tr><th>起付额</th><td>0.005 BTC</td></tr><tr><th>日理论收益</th><td>(.*?) BTC 每 Thash/s</td></tr>");
            //存入数据库
            BigDecimal s1 = new BigDecimal(s).multiply(new BigDecimal(0.975));
            config.setCoin(s1.setScale(10,BigDecimal.ROUND_DOWN));
            emailService.mail(config.getAdminEmail(),"理论收益已更新，理论值："+s, "系统通知-理论出币更新通知");
            log.info("理论出币已更新！");
            configDao.save(config);
        }catch (Exception e){
            log.error("错误！:"+e);
        }
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void btcPrice(){
        Optional<Config> configOptional = configDao.findById(1);
        if (!configOptional.isPresent())
            return;
        Config config = configOptional.get();
        try {
            int i = 0;
            String baData = HttpUtil.doGet("https://www.binance.co/api/v3/ticker/price?symbol=BTCUSDT");
            while (baData == null){
                baData = HttpUtil.doGet("https://www.binance.co/api/v3/ticker/price?symbol=BTCUSDT");
                i++;
                if (i == 10){
                    emailService.mail(config.getAdminEmail(),"【每日币价更新】币安api请求超过10次为成功，请知悉","系统通知-理论币价更新错误通知");
                    break;
                }
            }
            Map baMap = new Gson().fromJson(baData,Map.class);
            Double ba = Double.valueOf((String)baMap.get("price"));
            int j = 0;
            String zbData = HttpUtil.doGet("http://api.zb.cn/data/v1/ticker?market=btc_usdt");
            while (zbData == null){
                zbData = HttpUtil.doGet("http://api.zb.cn/data/v1/ticker?market=btc_usdt");
                j++;
                if (j == 10){
                    emailService.mail(config.getAdminEmail(),"【每日币价更新】ZBapi请求超过10次为成功，请知悉","系统通知-理论币价更新错误通知");
                    break;
                }
            }
            Map zbMap =(Map) new Gson().fromJson(zbData,Map.class).get("ticker");
            Double zb = Double.valueOf((String)zbMap.get("low"));
            //抓取汇率
            String hlData = HttpUtil.doGet("http://apicloud.mob.com/exchange/code/query?key=2a62eed51b0f2&code=usdcny");
            Double hl = 6.7;
            if (hlData != null){
                Map map = new Gson().fromJson(hlData,Map.class);
                Map map1 = (Map) map.get("result");
                hl = Double.valueOf((String) map1.get("closePri"));
            }

            if (zb > ba){
                config.setBtcExchange(zb * hl);
                emailService.mail(config.getAdminEmail(),"理论收益已更新，理论值："+zb * hl, "系统通知-理论出币更新通知");
            }else if (ba > zb){
                config.setBtcExchange(ba * hl);
                emailService.mail(config.getAdminEmail(),"理论收益已更新，理论值："+ba * hl, "系统通知-理论出币更新通知");
            }else {
                config.setBtcExchange(25106.11);
            }
            configDao.save(config);
        }catch (Exception e){
            log.error("错误！:"+e);
        }
    }

    public Json selectConfig(){
        return API.Success(configDao.findById(1).get());
    }


    public Json updateConfig(Config config){
        if (!Regular.isEntity(config))
            return API.error("参数不正确");
        Optional<Config> configOptional = configDao.findById(1);
        Config config1 = configOptional.get();
        if (config.getBtcExchange() != null)
            config1.setBtcExchange(config.getBtcExchange());
        if (config.getCoin() != null)
            config1.setCoin(config.getCoin());
        if (config.getDesPass() != null && !"".equals(config.getDesPass()))
            config1.setDesPass(config.getDesPass());
        if (config.getSaltLength() != null)
            config1.setSaltLength(config.getSaltLength());
        configDao.save(config1);
        return API.Success(config1);
    }

    //资产每日计算
    @Scheduled(cron = "0 0/30 1 * * ?")
    public void Income(){//后续有需要请分批次计算
        Optional<Config> configOptional = configDao.findById(1);
        Config config = configOptional.get();
        //获取所有用户
        List<User> users = userDao.findAll();
        if (users == null)
            return;
          for (User user:users) {
            //获取用户名下资产
            List<Assets> assetss = assetsDao.findByAssetsUserId(user.getUserId());
            if (assetss != null) {
                for (Assets assets : assetss) {
                    if (!Time.isSameDate(new Date(),assets.getAssetsTime())) {//如果当前天有数据则不进行计算
                        if ("BTC".equals(assets.getAssetsType())) {//资产类型
                            if (!"0".equals(assets.getAssetsTerm())) {//期限产品
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
                            } else {//非期限商品
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
                                                //查验当前是否有在提现中的订单
                                                List<Reflect> reflectList = reflectDao.findByAssetsId(assets.getAssetsPayId());
                                                BigDecimal reNum = new BigDecimal(0).setScale(16, BigDecimal.ROUND_DOWN);
                                                for (Reflect reflect : reflectList) {
                                                    if (!"0".equals(reflect.getState()))
                                                        reNum = reNum.add(reflect.getNum());
                                                }
                                                //换算成当前币值
                                                BigDecimal BTCPowerRate = new BigDecimal(totalPowerRate / config.getBtcExchange());//模拟值
                                                assets.setDeductions("" + BTCPowerRate);
                                                assets.setAssetsProfit(((profit.subtract(BTCPowerRate)).subtract(assets.getAssetsAvailableProfit()).subtract(reNum)).setScale(16, BigDecimal.ROUND_DOWN));
                                                assets.setAssetsAllProfit(profit.subtract(BTCPowerRate).setScale(16, BigDecimal.ROUND_DOWN));
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
                                        assets.setAssetsState("2");
                                        assets.setRemark("资产已经发放了所有收益");
                                    } else if ("3".equals(assets.getAssetsState())) {//资产已经被管理员取消
                                        assets.setAssetsState("3");
                                        assets.setRemark("资产已经被管理员取消");
                                    }
                                }
                            }
                            assetsDao.save(assets);
                        }//接eth收益计算
                    }

                }
            }
          }
        }
    }

    //汇率  http://op.juhe.cn/onebox/exchange/currency?from=usd&to=cny&key=d968d298a19abe42af4cb884ebd5d8b9
    //币安中国站  https://www.binance.co/api/v3/ticker/price?symbol=BTCUSDT
    //火币 需要梯子  https://api.huobi.pro/market/detail?symbol=btcusdt
    //ZB http://api.zb.cn/data/v1/ticker?market=btc_usdt


