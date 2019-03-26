package com.henglong.cloud.service;

import com.henglong.cloud.dao.*;
import com.henglong.cloud.entity.*;
import com.henglong.cloud.util.*;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);


    private String[] types = {".jpg", ".bmp", ".jpeg", ".png", ".JPG", ".BMP", ".JPEG", ".PNG"};

    private final PayDao payDao;

    private final CommodityDao commodityDao;

    private final UserDao userDao;

    private final OrderDao orderDao;

    private final MaintainDao maintainDao;

    @Autowired
    public FileService(PayDao payDao, CommodityDao commodityDao, UserDao userDao,OrderDao orderDao,MaintainDao maintainDao) {
        this.payDao = payDao;
        this.commodityDao = commodityDao;
        this.userDao = userDao;
        this.orderDao = orderDao;
        this.maintainDao = maintainDao;
    }


    public Json PayFile(MultipartFile file, String id) throws Exception {
        if (!Regular.isSql(id))
            return API.error("id不合法");
        //获取登录用户
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        //查询支付订单是否存在
        log.info("上传方法，接受到：" + id);
        String name = "";
        Pay pay = payDao.findByPayIdAndPayUserId(id, userId);
        if ((""+ CodeConstant.SUCCESS).equals(pay.getVoucherState()))
            return API.error("该订单已经审核通过");
        if (pay == null || file == null) {
            return API.error("支付订单不存在或者文件未选择");
        } else {
            if (file.getSize() > 2097152)
                return API.error("文件太大");
            if (pay.getVoucherState() != "1") {
                if (!file.isEmpty()) {
                    String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    name = id + "" + type;
                    if (Arrays.asList(types).contains(type)) {
                        BufferedOutputStream out = null;
                        FileOutputStream outs = null;
                        log.info(LoadFile.Path());
                        File fileSourcePath = new File(LoadFile.Path() + "/img/pay");
                        if (!fileSourcePath.exists()) {
                            fileSourcePath.mkdirs();
                        }
                        log.info("上传的文件名为：" + name);
                        outs = new FileOutputStream(new File(fileSourcePath, name));
                        out = new BufferedOutputStream(outs);
                        out.write(file.getBytes());
                        out.flush();
                        outs.flush();
                        out.close();
                        outs.close();
                        log.info("将存储地址储存到数据库");
                        pay.setVoucherUrl("/pay/pay_img/" + name);
                        log.info("变更支付订单截图上传状态");
                        pay.setVoucherState("1");
                        notice.examineNotice("支付审核",userId);
                        Order order = orderDao.findByOrderId(pay.getPayOrderId());
                        if (order != null) {
                            order.setOrderState("" + CodeConstant.PAY);
                            orderDao.save(order);
                        }
                        Optional<Maintain> maintainOptional = maintainDao.findByMaintainId(pay.getPayOrderId());
                        if (maintainOptional.isPresent()){
                            Maintain maintain = maintainOptional.get();
                            maintain.setState(CodeConstant.PAY);
                            maintainDao.save(maintain);
                        }
                        payDao.save(pay);

                        return API.Success(MessageUtils.get("file.ok"));
                    } else {
                        return API.error(MessageUtils.get("file.format"));
                    }
                }
                return API.error(MessageUtils.get("file.format"));
            }else {
                return API.error(MessageUtils.get("file.upload"));
            }
        }
    }

    public Json CommodityFile(MultipartFile file, String id) throws Exception {
        if (!Regular.isSql(id))
            return API.error("id不合法");
        String name = "";
        Commodity commodity = commodityDao.findByCommodityId(id);
        if (commodity == null)
            return API.error(MessageUtils.get("commodity.existent"));
        if (file == null) {
            return API.error(MessageUtils.get("file.choice"));
        } else {
            if (file.getSize() > 2097152)
                return API.error("文件太大");
            if (!file.isEmpty()) {
                String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String name2 = file.getOriginalFilename();
                name = id + "-" + name2;
                if (Arrays.asList(types).contains(type)) {
                    BufferedOutputStream out = null;
                    FileOutputStream outs = null;
                    File fileSourcePath = new File(LoadFile.Path() + "/img/Commodity");
                    if (!fileSourcePath.exists()) {
                        fileSourcePath.mkdirs();
                    }
                    log.info("上传的文件名为：" + name);
                    outs = new FileOutputStream(new File(fileSourcePath, name));
                    out = new BufferedOutputStream(outs);
                    out.write(file.getBytes());
                    out.flush();
                    outs.flush();
                    out.close();
                    outs.close();
                    log.info("将存储地址储存到数据库");
                    List<String> list = commodity.getCommodityUrl();
                    list.add("/Commodity/commodity_img/" + name);
                    commodity.setCommodityUrl(list);
                    commodityDao.save(commodity);
                    return API.Success(MessageUtils.get("file.ok"));
                }
                return API.error(MessageUtils.get("file.format"));
            }
            return API.error(MessageUtils.get("file.choice"));
        }
    }

    public Json UserFile(MultipartFile file) throws Exception {
        //获取手机号
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        String name = "";
        User user = userDao.findByUserId(userId);
        if (user == null) {
            return API.error(MessageUtils.get("user.unregistered"));
        }else if (file == null){
            return API.error(MessageUtils.get("file.choice"));
        } else {
            if (file.getSize() > 2097152)
                return API.error("文件太大");
            if (!file.isEmpty()) {
                String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                name = userId + "" + type;
                if (Arrays.asList(types).contains(type)) {
                    BufferedOutputStream out = null;
                    FileOutputStream outs = null;
                    File fileSourcePath = new File(LoadFile.Path() + "/img/user");
                    if (!fileSourcePath.exists()) {
                        fileSourcePath.mkdirs();
                    }
                    log.info("上传的文件名为：" + name);
                    outs = new FileOutputStream(new File(fileSourcePath, name));
                    out = new BufferedOutputStream(outs);
                    out.write(file.getBytes());
                    out.flush();
                    outs.flush();
                    out.close();
                    outs.close();
                    log.info("将存储地址储存到数据库");
                    user.setImgUrl("/user/user_img/" + name);
                    userDao.save(user);
                    return API.Success("/user/user_img/" + name);
                }
                return API.error(MessageUtils.get("file.format"));
            }
            return API.error(MessageUtils.get("file.choice"));
        }
    }

    public Json IDCardImg(MultipartFile cardImgA, MultipartFile cardImgB,MultipartFile cardImgC, String cardNo,String name) throws Exception {
        if (!Regular.isSql(cardNo))
            return API.error("参数不合法");
        if (!Regular.isSql(name))
            return API.error("名字不合法");
        Map<String,String> map = new HashMap<>();
        //获取手机号
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        //判断是否实名认证
        User user = userDao.findByUserId(userId);
        if (user == null)
            return API.error(MessageUtils.get("user.unregistered"));
        if (user.getUserStart() == 0)
            return API.error(MessageUtils.get("user.real.ok"));
        if (cardImgA == null || cardImgB == null || cardImgC == null ||"".equals(cardNo))
            return API.error(MessageUtils.get("user.real.expire"));
        if (!cardImgA.isEmpty()) {
            if (cardImgA.getSize() > 2097152 || cardImgB.getSize() > 2097152 || cardImgC.getSize() > 2097152)
                return API.error("文件太大");
            String type = cardImgA.getOriginalFilename().substring(cardImgA.getOriginalFilename().lastIndexOf("."));
            String type2 = cardImgB.getOriginalFilename().substring(cardImgA.getOriginalFilename().lastIndexOf("."));
            if (Arrays.asList(types).contains(type)&&Arrays.asList(types).contains(type2)) {
                File fileSourcePath = new File(LoadFile.Path() + "/img/user/"+userId);
                if (!fileSourcePath.exists()) {
                    fileSourcePath.mkdirs();
                }
                //上传正面
                log.info("用户【" + userId + "】，上传的文件名为：" + cardNo + "_A" + type);
                FileOutputStream outs = new FileOutputStream(new File(fileSourcePath, cardNo + "_A" + type));
                BufferedOutputStream out = new BufferedOutputStream(outs);
                out.write(cardImgA.getBytes());
                map.put("card_A","/user/card_img/"+userId + "/"+cardNo+"_A" + type);
                out.flush();
                outs.flush();
                out.close();
                outs.close();
                //上传反面
                log.info("用户【" + userId + "】，上传的文件名为：" + cardNo + "_B" + type);
                FileOutputStream outb = new FileOutputStream(new File(fileSourcePath, cardNo + "_B" + type));
                BufferedOutputStream outsb = new BufferedOutputStream(outb);
                outsb.write(cardImgB.getBytes());
                map.put("card_B","/user/card_img/"+userId + "/"+cardNo+"_B" + type);
                outb.flush();
                outsb.flush();
                outb.close();
                outsb.close();
                log.info("用户【" + userId + "】，上传的文件名为：" + cardNo + "_C" + type);
                FileOutputStream outc = new FileOutputStream(new File(fileSourcePath, cardNo + "_C" + type));
                BufferedOutputStream outsc = new BufferedOutputStream(outc);
                outsc.write(cardImgC.getBytes());
                map.put("card_C","/user/card_img/"+userId + "/"+cardNo+"_C" + type);
                outsc.flush();
                outc.flush();
                outsc.close();
                outc.close();
                //写入数据库
                user.setUserStart(1);
                user.setIDCardImg(map);
                user.setIDName(name);
                user.setIDCardNo(cardNo);
                notice.examineNotice("实名审核",userId);//通知
                userDao.save(user);
                return API.Success(MessageUtils.get("file.ok"));
            } else {
                return API.error(MessageUtils.get("file.format"));
            }
        }
        return API.error(MessageUtils.get("file.choice"));
    }
}


