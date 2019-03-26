package com.henglong.cloud.service;

import com.henglong.cloud.dao.AssetsDao;
import com.henglong.cloud.dao.PayDao;
import com.henglong.cloud.dao.SpreadDao;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.entity.Spread;
import com.henglong.cloud.util.*;
import com.henglong.cloud.util.aop.aopName.Profit;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SpreadService {

    private final static Logger log = LoggerFactory.getLogger(SpreadService.class);

    @Autowired
    private SpreadDao spreadDao;

    @Autowired
    private OnlyId onlyId;

    @Autowired
    private AssetsDao assetsDao;

    @Autowired
    private PayDao payDao;

    public Json SpreadAllInfo() {
        return API.Success(spreadDao.findAll());
    }



    //个人推广查看
    @Profit()
    public Json SpreadOneInfo() {
        //获取登录用户
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        List<Spread> As = new ArrayList<>();
        List<Spread> Bs = new ArrayList<>();
        Map Am = new HashMap();
        Map Bm = new HashMap();
        //获取推广码
        Spread spread = spreadDao.findByUserId(userId);
        //获取A类推荐
        As = spreadDao.findBySpreadCode(spread.getSpreadPromoCode());
        //z转换数组
        Spread[] AS = new Spread[As.size()];
        As.toArray(AS);
        Spread[] spread2 = new Spread[As.size()];
        //获取B类推荐
        for (int i = 0; i < As.size(); i++) {
            Bs = spreadDao.findBySpreadCode(As.get(i).getSpreadPromoCode());
            Spread[] BS = new Spread[Bs.size()];
            Bs.toArray(BS);
            //查询订单
            List<Assets> AAssets = assetsDao.findByAssetsUserId(As.get(i).getUserId());
            for (Assets assets : AAssets) {
                Optional<Pay> optionalPay = payDao.findByPayId(assets.getAssetsPayId());
                if (optionalPay.isPresent())
                    return API.error("获取推广信息出错");
                Pay pay = optionalPay.get();
                Am.put(pay.getPayId(), Double.valueOf(pay.getPayCommodityMoney()) * 0.03);
            }
            for (Spread Bsss : Bs) {
                List<Assets> BAssets = assetsDao.findByAssetsUserId(Bsss.getUserId());
                for (Assets assets : BAssets) {
                    Optional<Pay> optionalPay = payDao.findByPayId(assets.getAssetsPayId());
                    if (optionalPay.isPresent())
                        return API.error("获取推广信息出错");
                    Pay pay = optionalPay.get();
                    Bm.put(pay.getPayId(), Double.valueOf(pay.getPayCommodityMoney()) * 0.01);
                }
            }
            spread2[i] = new Spread();
            spread2[i].setId(As.get(i).getId());
            spread2[i].setSpreadPhone(As.get(i).getSpreadPhone());
            spread2[i].setSpreadPromoCode(As.get(i).getSpreadPromoCode());
            spread2[i].setSpreadUrl(As.get(i).getSpreadUrl());
            spread2[i].setSpreadNum(As.get(i).getSpreadNum());
            spread2[i].setSpreadMoney(As.get(i).getSpreadMoney());
            spread2[i].setSpreadCode(As.get(i).getSpreadCode());
            spread2[i].setProfit(Bm);
            spread2[i].setSpreadUser(BS);
        }

        Spread spread1 = new Spread();
        spread1.setId(spread.getId());
        spread1.setSpreadPhone(spread.getSpreadPhone());
        spread1.setSpreadPromoCode(spread.getSpreadPromoCode());
        spread1.setSpreadUrl(spread.getSpreadUrl());
        spread1.setSpreadNum(spread.getSpreadNum());
        spread1.setSpreadMoney(spread.getSpreadMoney());
        spread1.setSpreadCode(spread.getSpreadCode());
        spread1.setProfit(Am);
        spread1.setSpreadUser(spread2);
        return API.Success(spread1);
    }

    //通过推荐码查看（管理）
    public Json SpreadInfo(String code) {
        return API.Success(spreadDao.findBySpreadPromoCode(code));
    }


    /**
     * 更新输入，严格
     *
     * @param num
     * @return
     */
    public Json SpreadNum(Integer num, String code) {
        Spread spread = spreadDao.findBySpreadPromoCode(code);
        spread.setSpreadNum(num);
        spreadDao.save(spread);
        return API.Success(spread);
    }

    //推广生成,手机号
    public String Spreads(String userId,String phone, String code) {
        Spread spread = new Spread();
        String s = "";
        while (true) {
            s = onlyId.RandomString(6);
            if (spreadDao.findBySpreadPromoCode(s) == null) {
                break;
            }
        }
        spread.setUserId(userId);
        spread.setSpreadPhone(phone);
        spread.setSpreadPromoCode(s);
        spread.setSpreadUrl("/sign_up/" + s);
        spread.setSpreadNum(0);
        spread.setSpreadMoney(0.0);
        spread.setSpreadCode(code);
        spreadDao.save(spread);
        return "推广码生成成功";
    }

    //推广生成，邮箱
    public String SpreadsEmail(String userId,String email, String code) {
        Spread spread = new Spread();
        String s = "";
        while (true) {
            s = onlyId.RandomString(6);
            if (spreadDao.findBySpreadPromoCode(s) == null) {
                break;
            }
        }
        spread.setUserId(userId);
        spread.setSpreadEmail(email);
        spread.setSpreadPromoCode(s);
        spread.setSpreadUrl("/sign_up/" + s);
        spread.setSpreadNum(0);
        spread.setSpreadMoney(0.0);
        spread.setSpreadCode(code);
        spreadDao.save(spread);
        return "推广码生成成功";
    }

    //推广收益计算
    //收益计算重写
    //收益计算重写

}
