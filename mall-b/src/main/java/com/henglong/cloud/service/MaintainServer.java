package com.henglong.cloud.service;

import com.henglong.cloud.dao.AssetsDao;
import com.henglong.cloud.dao.MaintainDao;
import com.henglong.cloud.dao.PayDao;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Maintain;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.util.*;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MaintainServer {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(MaintainServer.class);

    private final AssetsDao assetsDao;

    private final OnlyId onlyId;

    private final PayDao payDao;

    private final MaintainDao maintainDao;

    @Autowired
    public MaintainServer(AssetsDao assetsDao, OnlyId onlyId, PayDao payDao, MaintainDao maintainDao) {
        this.assetsDao = assetsDao;
        this.onlyId = onlyId;
        this.payDao = payDao;
        this.maintainDao = maintainDao;
    }

    /**
     * 维护费下单
     * @param assetsId 资产id
     * @param num 缴纳天数
     * @return
     */
    public Json collection(String assetsId,Integer num){
        if (!Regular.isSql(assetsId))
            return API.error("参数不正确");
        //查询assets
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        Assets assets = assetsDao.findByAssetsPayIdAndAssetsUserId(assetsId,userId);
        if (assets == null)
            return API.error("没有这笔资产给你续费哦");
        //判断是否已经有足够的维护费订单
        List<Maintain> maintainList = maintainDao.findByAssetsId(assetsId);
        for (Maintain maintain:maintainList) {
            if (0 != maintain.getState())
                return API.error("同一个资产只能同时拥有一笔未完成的维护费订单！");
        }
        //判断资产是否已经缴齐维护费
        if (!"0".equals(assets.getAssetsTerm())) {//非永久资产
            if (assets.getMaintainDay().equals(Integer.valueOf(assets.getAssetsTerm())))
                return API.error("这笔资产已经不用再缴纳维护费了");
        }
        if (assets.getMaintainPayType() == 0)
            return API.error("这笔资产已经不用再缴纳维护费了");
        //计算单位费用
        double a = ((assets.getWatt() / 1000) * assets.getPowerRate()) * 24;//单位费用

        //缴纳天数是否大于欠费
        if (num < (Integer.valueOf(assets.getAssetsDay()) - assets.getMaintainDay()))
            return API.Success("缴纳的费用不足以补齐欠费哦");

        Optional<Pay> payOptional = payDao.findByPayId(assetsId);
        if (!payOptional.isPresent())
            return API.error("没有资产流水信息");
        Pay pay = payOptional.get();

        Maintain maintain = new Maintain();
        maintain.setMaintainId("MN"+onlyId.MaintainId());
        maintain.setMoney(a * Double.valueOf(num) * Double.valueOf(assets.getAssetsNum()));
        maintain.setUserId(userId);
        maintain.setCommodityId(pay.getPayCommodityId());
        maintain.setAssetsId(assetsId);
        maintain.setTerm(num);
        maintain.setTime(new Date());
        maintain.setState(CodeConstant.PAYMENT);

        maintainDao.save(maintain);
        return API.Success(maintain);
    }

    /**
     * 用户维护费查询
     * @param index
     * @param size
     * @param state 维护费状态
     * @return
     */
    public Json maintainPage(Integer index,Integer size,Integer state){
        if (index == null)
            index = 0;
        if (size == null)
            size = 10;
        long i = 0;
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        Map<String,Object> map = new HashMap<>();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(index,size,sort);
        Page<Maintain> maintainPage = null;
        if (state != null){
            maintainPage = maintainDao.findByUserIdAndState(userId,state,pageable);
//            map.put("count",maintainDao.countByUserIdAndState(userId,state));
        }else {
            maintainPage = maintainDao.findByUserId(userId, pageable);
//            map.put("count",maintainDao.countByUserId(userId));
        }
        List<Maintain> maintainList = new ArrayList<>();
        if (maintainPage == null)
            return API.error("未查询到维护费订单");
        for (Maintain maintain:maintainPage) {
            if (maintain.getState() != 14) {
                i = i + 1;
                maintainList.add(maintain);
            }
        }
        map.put("count",i);
        map.put("list",maintainList);
        return API.Success(map);
    }

    public Json adminMaintain(Integer page,Integer limit,String id,Integer state){
        if (!Regular.isSql(id)){
            return API.error("参数错误");
        }
        if (page == null) {
            page = 0;
        }else {
            page = page - 1;
        }
        if (limit == null || 0 == limit){
            limit = 10;
        }
        Pageable pageable = PageRequest.of(page,limit);
        List<Maintain> maintains = new ArrayList<>();
        if (id != null && !"".equals(id)){
            maintains.add(maintainDao.findByMaintainId(id).get());
        }else if (state != null){
            Page<Maintain> maintainPage = maintainDao.findByState(state,pageable);
            if (maintainPage == null)
                return API.Success();
            for (Maintain maintain:
                 maintainPage) {
                maintains.add(maintain);
            }
        }else {
            Page<Maintain> maintainPage = maintainDao.findAll(pageable);
            if (maintainPage == null)
                return API.Success();
            for (Maintain maintain:
                    maintainPage) {
                maintains.add(maintain);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("size",maintainDao.count());
        map.put("data",maintains);
        return API.Success(map);
    }

    public Json updateMaintain(Maintain maintain){
        if (maintain.getId() == null)
            return API.error("参数不正确");
        if (!Regular.isEntity(maintain))
            return API.error("参数非法");
        maintainDao.save(maintain);
        return API.Success(maintain);
    }
}
