package com.henglong.cloud.service;

import com.henglong.cloud.dao.AssetsDao;
import com.henglong.cloud.dao.PutForwardDao;
import com.henglong.cloud.dao.UserDao;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.PutForward;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.MessageUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class PutForwardService {

    private final PutForwardDao putForwardDao;

    private final AssetsDao assetsDao;

    private final UserDao userDao;

    @Autowired
    public PutForwardService(AssetsDao assetsDao, PutForwardDao putForwardDao, UserDao userDao) {
        this.assetsDao = assetsDao;
        this.putForwardDao = putForwardDao;
        this.userDao = userDao;
    }

    public Json Put(String assetsPayId, BigDecimal num){
        //获取当前操作人
        String userId=(String) SecurityUtils.getSubject().getPrincipal();
        //查询用户
        User user = userDao.findByUserId(userId);
        if (user == null)
            return API.error(MessageUtils.get("user.unregistered"));
        //查询资产
        Assets assets = assetsDao.findByAssetsPayId(assetsPayId);
        if (assets == null)
            return API.error(MessageUtils.get("put.assets.no"));
        //判断用户是否有这么多资产
        if (num.compareTo(assets.getAssetsProfit()) >= 0){
            //写入提现
            PutForward putForward = new PutForward();
            putForward.setAssetsId(assetsPayId);
            putForward.setType(assets.getAssetsType());
            putForward.setWallet((String) user.getWallet().get(assets.getAssetsType()));
            putForward.setNum(num);
            putForward.setName(user.getName());
            putForward.setPhone(user.getPhone());
            putForward.setEmail(user.getEmail());
            putForward.setSubmissionTime(new Date());
            putForward.setState(1);
            putForward.setAdoptTime(null);
            putForward.setHash("");
            putForwardDao.save(putForward);
            return API.Success(putForward);
        }else {
            return API.error(MessageUtils.get("put.assets.num.no"));
        }
    }

    public Json PutS(String asstes,String hash,Boolean b){
            //判断是否有订单
        PutForward putForward = putForwardDao.findByAssetsPayId(asstes);
        if (putForward == null)
            return API.error("当前订单不存在");
        if(putForward.getState() == 0)
            return API.error("审核过了");
        if (b){
            putForward.setState(0);
            putForward.setHash(hash);
            putForwardDao.save(putForward);
        }else {
            putForward.setState(4);
            putForward.setHash("提现被拒绝");
            putForwardDao.save(putForward);
        }
        return API.Success(putForward);
    }
}
