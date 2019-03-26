package com.henglong.cloud.service.other;

import com.henglong.cloud.controller.admin.user;
import com.henglong.cloud.dao.AssetsDao;
import com.henglong.cloud.dao.ConfigDao;
import com.henglong.cloud.dao.UserDao;
import com.henglong.cloud.dao.other.AssetsDateDao;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Config;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.entity.other.AssetsDate;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.Time;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 每日明细处理类
 */
@Component
public class AssetsDateServer {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(AssetsDateServer.class);

    private final AssetsDao assetsDao;

    private final UserDao userDao;

    private final AssetsDateDao assetsDateDao;

    private final ConfigDao configDao;

    @Autowired
    public AssetsDateServer(AssetsDao assetsDao, UserDao userDao, AssetsDateDao assetsDateDao, ConfigDao configDao) {
        this.assetsDao = assetsDao;
        this.userDao = userDao;
        this.assetsDateDao = assetsDateDao;
        this.configDao = configDao;
    }

    @Scheduled(cron = "0 0/25 0 * * ?")
    public void assetsDateUpdate() throws ParseException {
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat1.parse(dateFormat1.format(new Date()));
        Calendar calendars = Calendar.getInstance();
        calendars.setTime(date);
        calendars.add(Calendar.DATE, -1);
        Config config = configDao.findById(1).get();
        //查询全部用户
        List<User> userList = userDao.findAll();
        if (userList == null)
            return;
        for (User user:userList) {
            //获取当前用户下所有资产
            List<Assets> assetsList = assetsDao.findByAssetsUserId(user.getUserId());
            if (assetsList != null) {
                BigDecimal BTCPowerRate = new BigDecimal(0);
                BigDecimal profit = new BigDecimal(0);
                for (Assets assets : assetsList) {
                    if (assetsDateDao.findByUserIdAndTime(user.getUserId(),calendars.getTime()) == null) {//如果当前天有数据则不进行计算
                        //判断资产开始
                        if ("0".equals(assets.getAssetsState()) || "1".equals(assets.getAssetsState()) || "4".equals(assets.getAssetsState())) {
                            if (Time.belongDateDay(date, assets.getAssetsTime(), 0)) {
                                if (!Time.belongDateDay(date, assets.getAssetsTime(), Integer.valueOf(assets.getAssetsTerm()) + 1)) {

                                    if (assets.getCuring() == null || assets.getCuring() == -1) {

                                        //计算当日收益
                                        BigDecimal num = new BigDecimal(Integer.valueOf(assets.getAssetsNum()));//获取资产数量
                                        BigDecimal decimal = config.getCoin();//获取理论收益
                                        BigDecimal unit = num.multiply(decimal);//单位日收益,数量*理论单位收益


                                        //计算电费
                                        Double powerRate = assets.getPowerRate() * (assets.getWatt() / 1000) * Double.valueOf(assets.getAssetsNum()) * 24;//一天的电费


//                                        BTCPowerRate = BTCPowerRate.add(new BigDecimal(powerRate / config.getBtcExchange()));//模拟值
                                        if (assets.getMaintainPayType() == 0) {//扣除模式
                                            if (BTCPowerRate.compareTo(unit) < 0) {//如果小于收益
                                                profit = profit.add(unit);//所有资产单天收益
                                                BTCPowerRate = BTCPowerRate.add(new BigDecimal(powerRate / config.getBtcExchange()));//模拟值
                                            } else {
                                                profit = profit.add(new BigDecimal(0));
                                                BTCPowerRate = BTCPowerRate.add(new BigDecimal(0));
                                            }
                                        } else if (assets.getMaintainPayType() == 1) {//预缴模式
                                            profit = profit.add(unit);//所有资产单天收益
                                            //换算成当前币值
                                            BTCPowerRate = BTCPowerRate.add(new BigDecimal(powerRate / config.getBtcExchange()));//模拟值
                                        }

                                        if (user.getProfit() != null)
                                            user.setProfit(user.getProfit().add(assets.getAssetsAllProfit()));
                                    }
                                }
                            }
                        }
                    }
                }
                if (profit.compareTo(new BigDecimal(0)) != 0) {//当日累计产出等于0则不计算
                    AssetsDate assetsDate = new AssetsDate();
                    assetsDate.setKuangchi("BTC.com");
                    assetsDate.setUserId(user.getUserId());

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DATE, -1);
                    assetsDate.setTime(calendar.getTime());
                    //
                    assetsDate.setProfit(profit.setScale(16, BigDecimal.ROUND_DOWN));
                    assetsDate.setPowerRate(BTCPowerRate.setScale(16, BigDecimal.ROUND_DOWN));
                    userDao.save(user);
                    assetsDateDao.save(assetsDate);
                }
            }
        }
        log.info("每日资产更新完成，时间：{}", new Date());
    }

    /**
     * 查询每日明细
     * @param index
     * @param size
     * @return
     */
    public Json assetsDatePage(Integer index, Integer size,String startTime, String stopTime){
        if (index == null)
            index = 0;
        if (size == null || size == 0)
            size =10;
        String userId=(String) SecurityUtils.getSubject().getPrincipal();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(index,size,sort);
        Map<String,Object> map = new HashMap<>();
        Specification<AssetsDate> querySpecifi = new Specification<AssetsDate>(){
            @Override
            public Predicate toPredicate(Root<AssetsDate> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("userId").as(String.class), userId));
                if (startTime != null && !"".equals(startTime)) {
                    //大于或等于传入时间
                    predicates.add(cb.greaterThanOrEqualTo(root.get("time").as(String.class), startTime));
                }
                if (stopTime != null && !"".equals(stopTime)) {
                    //小于或等于传入时间
                    predicates.add(cb.lessThanOrEqualTo(root.get("time").as(String.class), stopTime));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        List<AssetsDate> assetsDateList = new ArrayList<>();
        Page<AssetsDate> assetsDatePage = null;
        if (startTime != null && !"".equals(startTime) && stopTime != null && !"".equals(stopTime)){
            assetsDatePage = assetsDateDao.findAll(querySpecifi,pageable);
            map.put("count",assetsDateDao.count(querySpecifi));
        }else {
            assetsDatePage = assetsDateDao.findByUserId(userId,pageable);
            map.put("count",assetsDateDao.countByUserId(userId));
        }
        if (assetsDatePage != null)
        for (AssetsDate assetsDate:assetsDatePage) {
            assetsDateList.add(assetsDate);
        }
        map.put("list",assetsDateList);
        return API.Success(map);
    }


}
