package com.henglong.cloud.service;

import com.henglong.cloud.dao.AssetsDao;
import com.henglong.cloud.dao.ReflectDao;
import com.henglong.cloud.dao.UserDao;
import com.henglong.cloud.entity.Assets;
import com.henglong.cloud.entity.Reflect;
import com.henglong.cloud.entity.User;
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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ReflectService {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(ReflectService.class);

    private final UserDao userDao;

    private final AssetsDao assetsDao;

    private final ReflectDao reflectDao;

    @Autowired
    public ReflectService(UserDao userDao, AssetsDao assetsDao, ReflectDao reflectDao) {
        this.userDao = userDao;
        this.assetsDao = assetsDao;
        this.reflectDao = reflectDao;
    }

    /**
     * 发起提现订单，仅支持BTC提现
     * @param assetsId 资产id
     * @param num 提现数量
     * @param remarks 备注
     * @return
     */
    public Json Reflects(String assetsId, BigDecimal num, String remarks) throws UnsupportedEncodingException {
        ////////数据核验区/////./././././././.
        if (num != null) {
            if (!(num.compareTo(new BigDecimal(0)) > 0)) {
                return API.error("数量不能小于或等于0");
            }
        }else {
            return API.error("请输入提现数量");
        }
        if (!Regular.isSql(assetsId))
            return API.error("资产单号不能有字母或特殊字符");
        if (!Regular.isSql(remarks)){
            return API.error("包含特殊字符哦");
        }
        ////////////././././././././././././.
        //创建提现订单
        Reflect reflect = new Reflect();
        String wallet = "";
        BigDecimal nums = null;
        //获取登录用户
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        //获取用户
        User user = userDao.findByUserId(userId);
        if (user == null)
            return API.error(MessageUtils.get("user.unregistered"));
        if (user.getIDCardNo() == null)
            return API.error(MessageUtils.get("user.real.no"));
        //查询资产
        Assets assets = assetsDao.findByAssetsPayIdAndAssetsUserId(assetsId,userId);
        if (assets == null)
            return API.error(MessageUtils.get("assets.existent"));
        if ("3".equals(assets.getAssetsState())){
            return API.error("当前资产正在核验中");
        }
        //查询同笔资产下是否有未完成的单子
        List<Reflect> reflectList = reflectDao.findByAssetsId(assets.getAssetsPayId());
        for (Reflect reflect1:reflectList) {
            if (!"0".equals(reflect1.getState()) && !"2".equals(reflect1.getState())) {
                return API.error("当前订单已经拥有一笔未完成的提现订单");
            }else {
                break;
            }
        }
        //计算资产//////////////////////////////////////////////////////////////////////
        if ("BTC".equals(assets.getAssetsType())) {
            if (assets.getAssetsProfit().compareTo(new BigDecimal(0.001)) < 1)//大于或等于
                return API.error(MessageUtils.get("put.assets.num.no"));
            reflect.setUserId(userId);
            reflect.setAssetsId(assetsId);
            reflect.setName(user.getName());
            reflect.setPhone(user.getPhone());
            reflect.setEmail(user.getEmail());
            reflect.setIDCard(user.getIDCardNo());
            reflect.setAssetsType(assets.getAssetsType());
            reflect.setBrokerage(new BigDecimal(0.001));
            nums = assets.getAssetsProfit();
            if (num.compareTo(nums) > 0)//小于或等于
                return API.error(MessageUtils.get("put.assets.num.no"));//不够提现
            //判断订单维护费缴费模式
            if (assets.getMaintainPayType() == 1){//预缴维护费
                //校验维护费是否交清
                Integer day = Time.differentDays(assets.getAssetsTime(), new Date());
                if (assets.getMaintainDay() - day >= 0){
                    reflect.setNum(num.setScale(16,BigDecimal.ROUND_DOWN));
                }else {
                    return API.error("请先交清维护费");
                }
            }else {//不预交维护费
                reflect.setNum(num.setScale(16,BigDecimal.ROUND_DOWN));//电费在计算收益时就已经扣除了
            }
            //扣除资产收益,变为冻结金额
            assets.setAssetsFrozenProfit(num.setScale(16,BigDecimal.ROUND_DOWN));
            assets.setAssetsProfit(nums.subtract(num).setScale(16,BigDecimal.ROUND_DOWN));
            wallet = (String) user.getWallet().get("BTC");
            if (wallet.equals(""))
                return API.error(MessageUtils.get("user.wallet.no"));
            reflect.setWallet(wallet);
            reflect.setState("3");
            notice.examineNotice("提现审核",userId);
            reflect.setRemarks(remarks);
        }else if ("ETH".equals(assets.getAssetsType())){//目前没有eth，暂时不写
            if (assets.getAssetsProfit().compareTo(new BigDecimal(10.00)) < 1)
                return API.error(MessageUtils.get("put.assets.num.no"));
            reflect.setAssetsId(assetsId);
            reflect.setName(user.getName());
            reflect.setPhone(user.getPhone());
            reflect.setEmail(user.getEmail());
            reflect.setIDCard(user.getIDCardNo());
            reflect.setAssetsType(assets.getAssetsType());
            nums = assets.getAssetsProfit();
            if (num.compareTo(nums) > 0)
                return API.error(MessageUtils.get("put.assets.num.no"));
            reflect.setNum(num);
            //扣除资产收益
            assets.setAssetsProfit(nums.subtract(num));
            wallet = (String) user.getWallet().get("ETH");
            if (wallet.equals(""))
                return API.error(MessageUtils.get("user.wallet.no"));
            reflect.setWallet(wallet);
            reflect.setState("3");
            reflect.setRemarks(remarks);
        }
        ////////////././././././././././././././././././././././././.
        reflect.setSubmitTime(new Date());
        reflectDao.save(reflect);
        assetsDao.save(assets);
        notice.smsReflect(user.getPhone(),""+num);
        return API.Success("成功，稍后将会有邮件提醒，请注意查收",reflect);
    }

    /**
     * 获取当前用户全部提现订单
     * @return
     */
    public Json reflectAll(){
        //获取用户
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return API.Success(reflectDao.findByUserId(userId));
    }

    public Json adminReflect(Integer index,Integer size,String state,Integer id){
        if (index == null){
            index = 0;
        }else {
            index = index - 1;
        }
        if (size == null || 0 == size){
            size = 10;
        }
        ///////////////////
        if (!Regular.isSql(state)){
            return API.error("状态码有误");
        }
        //////////////////
        Map<String,Object> map = new HashMap<>();
        Pageable pageable = PageRequest.of(index,size);
        List<Reflect> reflects = new ArrayList<>();
        if (id != null && id != 0){
            reflects.add(reflectDao.findById(id).get());
        }else if (state != null && !"".equals(state)){
            Page<Reflect> reflectPage = reflectDao.findByState(state,pageable);
            if (reflectPage == null)
                return API.Success();
            for (Reflect r :
                    reflectPage) {
                reflects.add(r);
            }
            map.put("size",reflectDao.countByState(state));
            map.put("data",reflects);
        }else {
            Page<Reflect> reflectPage = reflectDao.findAll(pageable);
            if (reflectPage == null)
                return API.Success();
            for (Reflect r :
                    reflectPage) {
                reflects.add(r);
            }
            map.put("size",reflectDao.count());
            map.put("data",reflects);
        }
        return API.Success(map);
    }


    /**
     * 分页查询
      * @param index
     * @param size
     * @param state 提现订单状态
     * @return
     */
    public Json reflectPage(Integer index,Integer size,String state){
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        if (index == null)
            index = 0;
        if (size == null || 0 == size)
            size = 10;

        if (!Regular.isSql(state)){
            return API.error("状态码有误");
        }

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(index,size,sort);
        Page<Reflect> reflectPage = null;
        Map<String,Object> map = new HashMap<>();
        if (state != null && !"".equals(state)){
            reflectPage = reflectDao.findByUserIdAndState(userId,state,pageable);
            map.put("count",reflectDao.countByUserIdAndState(userId,state));
        }else {
            reflectPage = reflectDao.findByUserId(userId, pageable);
            map.put("count",reflectDao.countByUserId(userId));
        }
        List<Reflect> reflectList = new ArrayList<>();
        for (Reflect reflect:reflectPage){
            reflectList.add(reflect);
        }
        map.put("list",reflectList);
        return API.Success(map);
    }

    /**
     * 审核，不打币
     * @param id 提现订单id
     * @param o 通过与否
     * @param remarks 备注
     * @return
     */
    public Json reflectExamine(Integer id, Integer o,String remarks) throws UnsupportedEncodingException {
        String userId = (String)SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        //查询提现订单
        Optional<Reflect> reflectOptional = reflectDao.findById(id);

        if (!Regular.isSql(remarks)){
            log.warn("管理员["+user.getName()+"],在提现订单审核时尝试使用SQL关键字执行操作，请尽快联系确认账号安全");
            notice.safeNotice(user.getName());
            return API.error("备注中包含SQL关键字，已记录当前操作！");
        }

        if (!reflectOptional.isPresent())
            return API.error("要审核的提现订单不存在");
        Reflect reflect = reflectOptional.get();
        //查询资产是否存在
        Assets assets = assetsDao.findByAssetsPayIdAndAssetsUserId(reflect.getAssetsId(),reflect.getUserId());
        if (assets == null)
            return API.error("提现订单与被提现资产不符，请仔细查验");
        //格式化o
        if (o == 0){//通过
            reflect.setState("1");
            reflect.setRemarks(remarks);
            reflect.setExamineUserId(userId);
            reflect.setExaminePhone(user.getPhone());
            reflect.setExamineTime(new Date());
            reflectDao.save(reflect);
            notice.smsReflectE(reflect.getPhone(),reflect.getAssetsId(),"已通过");
        }else if (o == 1){//拒绝
            reflect.setState("2");//不可被操作状态
            reflect.setRemarks(remarks);
            reflect.setExamineUserId(userId);
            reflect.setExaminePhone(user.getPhone());
            reflect.setExamineTime(new Date());
            reflectDao.save(reflect);
            notice.smsReflectE(reflect.getPhone(),reflect.getAssetsId(),"已拒绝");
            //返还冻结收益
            assets.setAssetsProfit(assets.getAssetsProfit().add(assets.getAssetsFrozenProfit()).setScale(16,BigDecimal.ROUND_DOWN));
            //减去冻结收益
            assets.setAssetsFrozenProfit(assets.getAssetsFrozenProfit().subtract(reflect.getNum()).setScale(16,BigDecimal.ROUND_DOWN));
            assetsDao.save(assets);
        }else {
            return API.error("审核参数错误");
        }
        return API.Success(reflect);
    }


    //审核，打币
    public Json reflectPayCoin(Integer id, BigDecimal num, BigDecimal brokerage,String hash) throws UnsupportedEncodingException {
        if (num.compareTo(new BigDecimal(0)) > 0){
            return API.error("数量不能小于或等于0");
        }
        if (brokerage.compareTo(new BigDecimal(0)) > 0){
            return API.error("数量不能小于或等于0");
        }
        String userId = (String)SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        if (Regular.isSql(hash)){
            log.warn("管理员["+user.getName()+"],在提现订单审核时尝试使用SQL关键字执行操作，请尽快联系确认账号安全");
            notice.safeNotice(user.getName());
            return API.error("hash存在SQL关键字，已记录当前操作！");
        }
        //查询提现订单
        Optional<Reflect> reflectOptional = reflectDao.findById(id);
        if (!reflectOptional.isPresent())
            return API.error("要支付的提现订单不存在");
        Reflect reflect = reflectOptional.get();
        //查询资产是否存在
        Assets assets = assetsDao.findByAssetsPayIdAndAssetsUserId(reflect.getAssetsId(),reflect.getUserId());
        if (assets == null)
            return API.error("提现订单与被提现资产不符，请仔细查验");
        //判断是否通过审核
        if ("1".equals(reflect.getState())){
            assets.setAssetsAvailableProfit(assets.getAssetsAvailableProfit().add(reflect.getNum()));//已提数量
            assets.setAssetsFrozenProfit(assets.getAssetsFrozenProfit().subtract(reflect.getNum()));//解除冻结
            assetsDao.save(assets);
            reflect.setActualNum(num);//转币数量
            reflect.setBrokerage(brokerage);//转账手续费
            reflect.setHash(hash);
            reflect.setTransferUserId(userId);
            reflect.setCompleteTime(new Date());
            reflect.setState("0");
            reflectDao.save(reflect);
            notice.smsReflectP(reflect.getPhone(),reflect.getAssetsId(),"已支付",reflect.getNum().toString());
        }else {
            return API.error("当前状态不允许支付");
        }
        return API.Success(reflect);
    }

    //管理员修改
    public Json updateReflect(Reflect reflect){
        if (reflect.getId() == null){
            return API.error("参数不正确");
        }
        if (reflect.getNum().compareTo(new BigDecimal(0)) > 0){
            return API.error("提现数量不能小于0");
        }
        reflectDao.save(reflect);
        return API.Success(reflect);

    }
}
