package com.henglong.cloud.util.aop.aopRealize;

import com.henglong.cloud.dao.AssetsDao;
import com.henglong.cloud.dao.PayDao;
import com.henglong.cloud.dao.SpreadDao;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Pay;
import com.henglong.cloud.entity.Spread;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Aspect
public class ProfitRealize {

    private static final Logger log = LoggerFactory.getLogger(ProfitRealize.class);

    private final SpreadDao spreadDao;

    private final AssetsDao assetsDao;

    private final PayDao payDao;

    @Autowired
    public ProfitRealize(SpreadDao spreadDao, AssetsDao assetsDao, PayDao payDao) {
        this.spreadDao = spreadDao;
        this.assetsDao = assetsDao;
        this.payDao = payDao;
    }


    @Pointcut("@annotation(com.henglong.cloud.util.aop.aopName.Profit)")
    public void Profit() {
    }


    @After("Profit()")
    public void Pro() throws Throwable {
        Double AProfit = 0.0;
        Double BProfit = 0.0;
        //获取当前用户推荐码
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        Spread spread = spreadDao.findByUserId(userId);
        String spreadCode = spread.getSpreadPromoCode();
        //获取当前用户推广的A类
        List<Spread> AList = spreadDao.findBySpreadCode(spreadCode);
        //获取当前用户推广的B类
        for (Spread ASpread : AList) {
            List<Spread> BList = spreadDao.findBySpreadCode(ASpread.getSpreadPromoCode());
            for (Spread BSpread : BList) {
                //获取B类推广用户资产
                List<Assets> BAssetsList = assetsDao.findByAssetsUserId(BSpread.getSpreadPhone());
                if (BAssetsList == null)
                    break;
                //查询订单
                for (Assets assets : BAssetsList) {
                    Optional<Pay> optionalPay = payDao.findByPayId(assets.getAssetsPayId());
                    if (optionalPay.isPresent())
                        return;
                    Pay pay = optionalPay.get();
                    log.info("当前计算B类订单ID为【"+assets.getAssetsPayId()+"】");
                    //计算提成
                    BProfit += Double.valueOf(pay.getPayCommodityMoney())*Double.valueOf("0.01");
                }
            }
            //获取推广A类用户资产
            List<Assets> AAssetsList = assetsDao.findByAssetsUserId(ASpread.getSpreadPhone());
            if (AAssetsList == null)
                break;
            for (Assets AAssets : AAssetsList){
                Optional<Pay> optionalPay = payDao.findByPayId(AAssets.getAssetsPayId());
                if (optionalPay.isPresent())
                    return;
                Pay pay = optionalPay.get();
                log.info("当前计算A类订单ID为【"+AAssets.getAssetsPayId()+"】");
                AProfit += Double.valueOf(pay.getPayCommodityMoney())*Double.valueOf("0.03");
            }
        }
        //将收益写入数据库
        log.info("用户【"+userId+"】，推广A类用户总收益为【"+AProfit+"】，推广B类用户总收益为【"+BProfit+"】");
        spread.setSpreadMoney(AProfit+BProfit);
        spreadDao.save(spread);
    }
}
