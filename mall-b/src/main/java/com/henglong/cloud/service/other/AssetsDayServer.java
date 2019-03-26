package com.henglong.cloud.service.other;

import com.henglong.cloud.dao.AssetsDao;
import com.henglong.cloud.dao.ConfigDao;
import com.henglong.cloud.dao.other.AssetsDayDao;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Config;
import com.henglong.cloud.entity.other.AssetsDay;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.Time;
import org.apache.shiro.SecurityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *每日每单详情处理
 */
@Component
public class AssetsDayServer  {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(AssetsDayServer.class);

    private final AssetsDayDao assetsDayDao;

    private final AssetsDao assetsDao;

    private final ConfigDao configDao;

    @Autowired
    public AssetsDayServer(AssetsDayDao assetsDayDao, AssetsDao assetsDao, ConfigDao configDao) {
        this.assetsDayDao = assetsDayDao;
        this.assetsDao = assetsDao;
        this.configDao = configDao;
    }


    @Scheduled(cron = "0 0/30 0 * * ?")
    public void AssetsDayUpdate() throws ParseException {
        //查询全部资产
        List<Assets> assetsList = assetsDao.findAll();
        if (assetsList == null)
            return;
        Config config = configDao.findById(1).get();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat1.parse(dateFormat1.format(new Date()));
        Calendar calendars = Calendar.getInstance();
        calendars.setTime(date);
        calendars.add(Calendar.DATE, -1);
        for (Assets assets:assetsList) {
            if (assetsDayDao.findByTimeAndAssetsId(calendars.getTime(),assets.getAssetsPayId()) == null) {//如果当前天有数据则不进行计算
                if ("0".equals(assets.getAssetsState()) || "1".equals(assets.getAssetsState()) || "4".equals(assets.getAssetsState())) {
                    if (Time.belongDateDay(date, assets.getAssetsTime(), 0)) {
                        if (!Time.belongDateDay(date, assets.getAssetsTime(), Integer.valueOf(assets.getAssetsTerm()) + 1)) {


                            if (assets.getCuring() == null || assets.getCuring() == -1) {
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
                                BigDecimal BTCPowerRate = new BigDecimal(powerRate / config.getBtcExchange());//模拟值

                                assetsDay.setPowerRate(BTCPowerRate.setScale(16, BigDecimal.ROUND_DOWN));

                                assetsDayDao.save(assetsDay);
                            }
                        }
                    }
                }
            }
        }
        log.info("每日每单资产更新完成，时间：{}", new Date());
    }


    public Json AssetsDayPage(Integer index,Integer size, Date time){
        if (index == null)
            index = 0;
        if (size == null || size == 0)
            size = 10;
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(index,size,sort);
        Page<AssetsDay> assetsDatePage = null;
        Map<String,Object> map = new HashMap<>();
        if (time != null) {
            assetsDatePage = assetsDayDao.findByUserIdAndTime((String) SecurityUtils.getSubject().getPrincipal(), time, pageable);
            map.put("count", assetsDayDao.countByUserIdAndTime((String) SecurityUtils.getSubject().getPrincipal(), time));
        }else {
            assetsDatePage = assetsDayDao.findByUserId((String) SecurityUtils.getSubject().getPrincipal(),pageable);
            map.put("count", assetsDayDao.countByUserId((String) SecurityUtils.getSubject().getPrincipal()));
        }
        List<AssetsDay> assetsDayList = new ArrayList<>();
        for (AssetsDay assetsDay: assetsDatePage){
            assetsDayList.add(assetsDay);
        }
        map.put("list",assetsDayList);
        return API.Success(map);
    }

}
