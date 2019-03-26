package com.henglong.cloud.service;

import com.henglong.cloud.dao.ConfigDao;
import com.henglong.cloud.dao.RealmDao;
import com.henglong.cloud.dao.RolesDao;
import com.henglong.cloud.dao.UserDao;
import com.henglong.cloud.entity.Address;
import com.henglong.cloud.entity.Config;
import com.henglong.cloud.entity.Roles;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private static final String modeurl = LoadFile.Path()+"/mode/";

    private final UserDao userDao;

    private final RolesDao rolesDao;

    private final RealmDao realmDao;

    private final EmailService emailservice;

    private final DES des;

    private final OnlyId onlyId;


    private final MySalt mySalt;

    private final FileService fileService;

    private final ConfigDao configDao;

    @Autowired
    public UserService(UserDao userDao, RolesDao rolesDao, RealmDao realmDao, EmailService emailservice, DES des, OnlyId onlyId, MySalt mySalt, FileService fileService, ConfigDao configDao) {
        this.userDao = userDao;
        this.rolesDao = rolesDao;
        this.realmDao = realmDao;
        this.emailservice = emailservice;
        this.des = des;
        this.onlyId = onlyId;
        this.mySalt = mySalt;
        this.fileService = fileService;
        this.configDao = configDao;
    }

    //获取登录用户信息
    public Json UserInfo(){
        //获取登录userid
        String userId=(String) SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        if (user != null) {
            List<Roles> roles = rolesDao.findByUserId(userId);
            List<String> userRoles = new ArrayList<>();
            for (Roles r : roles) {
                userRoles.add(r.getRole());
            }
            user.setRoles(userRoles);
            if (user.getIDCardNo() != null && user.getIDCardNo().length() == 18)
               user.setIDCardNo(user.getIDCardNo().substring(0,2)+"************"+user.getIDCardNo().substring(14));
            user.setIDCardImg(null);
        }
        return API.Success(user);
    }

    //更新用户信息
    public Json UserAdd(User users){
        //获取手机号
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        if (users.getName() != null && !"".equals(users.getName()))
        user.setName(users.getName());
        return API.Success(userDao.save(user));
    }

    public Json UserAdminAdd(User user){
        Config config = configDao.findById(1).get();
        User user1 = userDao.findByUserId(user.getUserId());
        if (user1 != null){
            user1.setId(user.getId());
            user1.setUserId(user.getUserId());
            user1.setPhone(user.getPhone());
            user1.setEmail(user.getEmail());
            user1.setName(user.getName());
            user1.setUserStart(user.getUserStart());
            user1.setRandomCode(user.getRandomCode());
            user1.setImgUrl(user.getImgUrl());
            user1.setIDCardNo(user.getIDCardNo());
            user1.setProfit(user.getProfit());
            user1.setRoles(user.getRoles());
            userDao.save(user1);
        }else {
            if (user.getPhone() != null || user.getEmail() != null) {
                String salt = MyMd5.Md5(mySalt.Salt(config.getSaltLength()), 1024);
                //加密密码
                String pass = MyMd5.Md5(user.getPassword(), salt, 1024);
                user.setUserId("" + new SnowflakeIdWorker(10, 10).nextId());
                user.setSalt(salt);
                user.setPassword(pass);
                if (user.getRoles() == null) {
                    Roles roles = new Roles();
                    roles.setUserId(user.getUserId());
                    roles.setRole("user");
                    rolesDao.save(roles);
                }
                if (user.getUserStart() == null)
                    user.setUserStart(2);
                userDao.save(user);
            } else {
                return API.error(MessageUtils.get("user.least"));
            }
        }
        return API.Success(user);
    }

    //添加收货地址
    public Json UserAddress(Address address){
        //获取用户信息
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        List<Address> addressList = user.getAddress();
        log.info("{}",address.getName());
        if (address.getName() == null || "".equals(address.getName()))
            return API.error(MessageUtils.get("user.address.name"));
        if (address.getPhone() == null || "".equals(address.getPhone()))
            return API.error(MessageUtils.get("user.address.phone"));
        if (address.getAddress() == null || "".equals(address.getAddress()))
            return API.error(MessageUtils.get("user.address"));
        if (address.getDefaults() == null) {
            address.setDefaults(1);
        }else {
            if (address.getDefaults() == 0){
                //去掉另一个默认
                for (Address address2:addressList) {
                    if (address2.getDefaults() == 0) {
                        addressList.remove(address2);
                        address2.setDefaults(1);
                        addressList.add(address2);
                        break;
                    }
                }
                address.setDefaults(0);
            }
            address.setDefaults(1);
        }
        addressList.add(address);
        user.setAddress(addressList);
        userDao.save(user);
        return API.Success(address);
    }

    //修改收货地址
    public Json UserUpdateAddress(Address address){
        //获取用户信息
        String userId = (String)SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        List<Address> addressList = user.getAddress();
        if (address.getId() == null )
            return API.error(MessageUtils.get("user.address.id"));
        for (Address address1 : addressList){
            if (address1.getId().equals(address.getId())){
                addressList.remove(address1);
                if (address.getPhone() != null && !"".equals(address.getPhone()))
                    address1.setPhone(address.getPhone());
                if (address.getName() != null && !"".equals(address.getName()))
                    address1.setName(address.getName());
                if (address.getAddress() != null && !"".equals(address.getAddress()))
                    address1.setAddress(address.getAddress());
                if (address.getEmail() != null && !"".equals(address.getEmail()))
                    address1.setEmail(address.getEmail());
                if (address.getLabel() != null && !"".equals(address.getLabel()))
                    address1.setLabel(address.getLabel());
                if (address.getDefaults() != null) {
                    if (address.getDefaults() == 0){
                        //去掉另一个默认
                        for (Address address2:addressList) {
                            if (address2.getDefaults() == 0) {
                                addressList.remove(address2);
                                address2.setDefaults(1);
                                addressList.add(address2);
                                break;
                            }
                        }
                    }
                    address1.setDefaults(address.getDefaults());
                }
                addressList.add(address1);
            }
        }
        user.setAddress(addressList);
        userDao.save(user);
        return API.Success(address);
    }

    //删除收货地址
    public Json UserDeleteAddress(Integer addressId){
        String userId = (String)SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        List<Address> addressList = user.getAddress();
        if (addressList == null)
            return API.error(MessageUtils.get("currency.query"));
        for (Address address1 : addressList) {
            if (address1.getId().equals(addressId)) {
                addressList.remove(address1);
                break;
            }
        }
        user.setAddress(addressList);
        userDao.save(user);
        return API.Success(addressList);
    }


    //发送邮件验证
    public void mail(String email) throws Exception {
        if (!Regular.isEmail(email)){
            return;
        }
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        if (user.getPhone() == null || "".equals(user.getPhone()))
            return;
        //生成确认连接
        String url="http://127.0.0.1/user/email_user_confirm?";
        /*准备加密*/
        BASE64Decoder decoder = new BASE64Decoder();
        //加密手机号
        url += "userId="+ java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(userId.getBytes("utf-8"),"szhl8888")), "UTF-8");
        //加密邮箱
        url += "&email="+ java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(email.getBytes("utf-8"),"szhl8888")), "UTF-8");
        //加密时间
        url += "&date="+ java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("utf-8"),"szhl8888")), "UTF-8") ;
        //获取模板
        String mayi = LoadFile.TemplateLoad(new File(modeurl + "mayi.html"));
        if (user.getEmail() != null && !"".equals(user.getEmail())){
            String s = mayi.replace("demo-title", MessageUtils.get("email.dome.title.2"))
                    .replace("demo-text.1", MessageUtils.get("email.dome.text.1"))
                    .replace("demo-text.2", MessageUtils.get("email.dome.text.2"))
                    .replace("demo-text.3", MessageUtils.get("email.dome.text.email")+email)
                    .replace("demo-text.url", url + "&code=" + java.net.URLEncoder.encode(onlyId.RandomString(4), "utf-8"))
                    .replace("demo-text.4", MessageUtils.get("email.dome.button.text"));
            emailservice.mail(user.getEmail(), s, MessageUtils.get("email.title"));
        }else {
            String s = mayi.replace("demo-title", MessageUtils.get("email.dome.title.2"))
                    .replace("demo-text.1", MessageUtils.get("email.dome.text.1"))
                    .replace("demo-text.2", MessageUtils.get("email.dome.text.2"))
                    .replace("demo-text.3", MessageUtils.get("email.dome.text.email")+email)
                    .replace("demo-text.url", url + "&code=" + java.net.URLEncoder.encode(onlyId.RandomString(4), "utf-8"))
                    .replace("demo-text.4", MessageUtils.get("email.dome.button.text"));
            emailservice.mail(email, s, MessageUtils.get("email.title.1"));
        }
    }

    //接受受邮件验证
    public Json MailConfirm(String userId,String email,String date,String code) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        if (userId == null || userId.equals(""))
            return API.error(MessageUtils.get("email.user"));
        if (email == null || email.equals(""))
            return API.error(MessageUtils.get("email.email"));
        if (date == null || date.equals(""))
            return API.error(MessageUtils.get("email.time"));
        if (code == null || code.equals(""))
            return API.error(MessageUtils.get("email.code"));
        User user = userDao.findByUserId(new String(des.decrypt(decoder.decodeBuffer(userId),"szhl8888")));
        if (user == null)
            return API.error(MessageUtils.get("email.user.error"));
        //验证重复连接
        if (code.equals(user.getRandomCode()))
            return API.error(MessageUtils.get("email.url.repeat"));
        //解密传入时间
        SimpleDateFormat simpleFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //
        String toDate = simpleFormat.format(new Date());
        long from = simpleFormat.parse(new String(des.decrypt(decoder.decodeBuffer(date),"szhl8888"))).getTime();
        long to = simpleFormat.parse(toDate).getTime();
        int minutes = (int) ((to - from)/(1000 * 60));
        //
        if (minutes >=30)
            return API.error(MessageUtils.get("email.url.invalid"));
        user.setEmail(new String(des.decrypt(decoder.decodeBuffer(email),"szhl8888")));
        user.setRandomCode(code);
        userDao.save(user);
        return API.Success(user);
    }

    //重置密码发送验证码
    public Json passwordPhoneCode(String phone, String email, HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
        response.setHeader("Cache-Control", "no-cache");
        String passwordPhoneCodeUpdate = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 6, null);
        request.getSession().setAttribute("password_phone_code_update", passwordPhoneCodeUpdate);
        //发送短信验证码
        if (phone != null && !"".equals(phone)){
            if (!Regular.isPhone(phone)) {
                return API.error("手机号不正确");
            }else {
                if (userDao.findByPhone(phone) == null) {
                    log.info("短信验证码：{}", passwordPhoneCodeUpdate);
                    return API.Success(notice.sms(phone, "139490", passwordPhoneCodeUpdate));
//                return API.Success(passwordPhoneCodeUpdate);
                }else {
                    return API.error("手机号已绑定");
                }
            }
        }else if (email != null && !"".equals(email)){
            if (userDao.findByEmail(email) == null) {
                emailservice.mail(email, "您的验证码：" + passwordPhoneCodeUpdate, "验证码");
                return API.Success("发送成功");
            }else {
                return API.error("邮箱已绑定");
            }
        }else {
            return API.error("请输入参数");
        }
    }

    //

    //用户绑定手机号
    public Json bindingPhone(String phone ,String email,String code) throws UnsupportedEncodingException {
        if (userDao.findByPhone(phone) != null)
            return API.error(MessageUtils.get("user.binding.phone"));
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("password_phone_code_update");
        if (validateCode == null)
            return API.error(MessageUtils.get("verification.obtain"));
        if (code == null || code.equals("")) {
            return API.error( MessageUtils.get("verification.empty"));
        }
        if (!validateCode.equalsIgnoreCase(code)) {
            return API.error(MessageUtils.get("verification.error"));
        }
        if ((user.getPhone() == null || "".equals(user.getPhone())) && (phone != null && !"".equals(phone))){
            if (userDao.findByPhone(phone) != null)
                return API.error("该手机号已被其他用户使用");
            if (Regular.isPhone(phone)) {
                user.setPhone(phone);
            }else {
                return API.error("手机号格式不正确");
            }
        }else if ((user.getEmail() == null || "".equals(user.getEmail())) && !"".equals(email)){
            if (userDao.findByEmail(email) != null)
                return API.error("该邮箱已被其他用户占用");
            if (Regular.isEmail(email)) {
                user.setEmail(email);
            }else {
                return API.error("邮箱格式不正确");
            }
        }else {
//            String url = "http://127.0.0.1/user/binding_phone?";
//            //加密手机号
//            url += "userId="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(userId.getBytes("utf-8"),"szhl8888")));
//            //拼接随机码
//            url += "&phone="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(phone.getBytes("utf-8"),"szhl8888")));
//            //拼接时间
//            url += "&date="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("utf-8"),"szhl8888")));
//            url += "&code=" + java.net.URLEncoder.encode(onlyId.RandomString(4), "utf-8");
//            //发送短信
//            String phone1 = user.getPhone();//发送验证码到这里
//            ////////////
            return API.error("您已绑定相关信息");
        }
        userDao.save(user);
        session.removeAttribute("password_phone_code_update");
        return API.Success();
    }

    //跟换绑定接受连接
    public Json binding(String userId,String phone,String date,String code) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        if (userId == null || "".equals(userId))
            return API.error(MessageUtils.get("email.user"));
        if (phone == null || "".equals(phone))
            return API.error(MessageUtils.get("email.phone"));
        if (date == null || "".equals(date))
            return API.error(MessageUtils.get("email.time"));
        if (code == null || "".equals(code))
            return API.error(MessageUtils.get("email.code"));
        User user = userDao.findByUserId(new String(des.decrypt(decoder.decodeBuffer(userId),"szhl8888")));
        if (user == null)
            return API.error(MessageUtils.get("email.user.error"));
        //验证连接重复
        if (code.equals(user.getRandomCode()))
            return API.error(MessageUtils.get("email.url.repeat"));
        //验证连接过期
        SimpleDateFormat simpleFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //
        String toDate = simpleFormat.format(new Date());
        long from = simpleFormat.parse(new String(des.decrypt(decoder.decodeBuffer(date),"szhl8888"))).getTime();
        long to = simpleFormat.parse(toDate).getTime();
        int minutes = (int) ((to - from)/(1000 * 60));
        //
        if (minutes >=30)
            return API.error(MessageUtils.get("email.url.invalid"));
        user.setPhone(new String(des.decrypt(decoder.decodeBuffer(phone),"szhl8888")));
        user.setRandomCode(code);
        userDao.save(user);
        return API.Success(user);
    }


    public Json passwordCode(String phone, String email, HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
        //发送短信验证码
        if (phone != null && !"".equals(phone)){
            if (!Regular.isPhone(phone)) {
                return API.error("手机号不正确");
            }else {
                if (userDao.findByPhone(phone) != null) {
                    response.setHeader("Cache-Control", "no-cache");
                    String passwordPhoneCodeUpdate = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_ONLY, 6, null);
                    request.getSession().setAttribute("password_phone_code_", passwordPhoneCodeUpdate);
                    log.info("短信验证码：{}", passwordPhoneCodeUpdate);
                    return API.Success(notice.sms(phone, "139490", passwordPhoneCodeUpdate));
//                return API.Success(passwordPhoneCodeUpdate);
                }else {
                    return API.error("手机号未注册");
                }
            }
        }else if (email != null && !"".equals(email)){
            if (userDao.findByEmail(email) != null) {
                response.setHeader("Cache-Control", "no-cache");
                String passwordPhoneCodeUpdate = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_ONLY, 6, null);
                request.getSession().setAttribute("password_email_code_", passwordPhoneCodeUpdate);
                emailservice.mail(email, "您的验证码：" + passwordPhoneCodeUpdate, "验证码");
                return API.Success("发送成功");
            }else {
                return API.error("邮箱未注册");
            }
        }else {
            return API.error("请输入参数");
        }
    }

    //忘记密码发送短信,改成验证码方式
    //可能遇到的bug，使用邮箱发送验证码，却填写了手机来重置
    public Json RetrievePassWord(String email,String phone,String password,String code) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        //确认账户已经注册
        User user = null;
        if (phone != null && !"".equals(phone)) {
            Session session = SecurityUtils.getSubject().getSession();
            String validateCode = (String) session.getAttribute("password_phone_code_");
            if (validateCode == null)
                return API.error(MessageUtils.get("verification.obtain"));
            if (code == null || code.equals("")) {
                return API.error( MessageUtils.get("verification.empty"));
            }
            if (!validateCode.equalsIgnoreCase(code)) {
                return API.error(MessageUtils.get("verification.error"));
            }
            user = userDao.findByPhone(phone);
            if (user == null)
                return API.error(MessageUtils.get("user.unregistered"));
            session.removeAttribute("password_phone_code_");
        }else if (email != null && !"".equals(email)){
            Session session = SecurityUtils.getSubject().getSession();
            String validateCode = (String) session.getAttribute("password_email_code_");
            if (validateCode == null)
                return API.error(MessageUtils.get("verification.obtain"));
            if (code == null || code.equals("")) {
                return API.error( MessageUtils.get("verification.empty"));
            }
            if (!validateCode.equalsIgnoreCase(code)) {
                return API.error(MessageUtils.get("verification.error"));
            }
            user = userDao.findByEmail(email);
            if (user == null)
                return API.error(MessageUtils.get("user.unregistered"));
            session.removeAttribute("password_email_code_");
        }else {
            return API.error("参数不正确");
        }
        String salts = MyMd5.Md5(mySalt.Salt(configDao.findById(1).get().getSaltLength()),1024);
        //加密密码
        String pas1=MyMd5.Md5(password,salts,1024);
        user.setSalt(salts);
        user.setPassword(pas1);
        userDao.save(user);
        return API.Success(user);
    }


    //重置密码邮箱方式
    public Json RetrievePassword(String email,String password) throws UnsupportedEncodingException {
        BASE64Decoder decoder = new BASE64Decoder();
        User user = userDao.findByEmail(email);
        if (user == null)
            return API.error(MessageUtils.get("user.unregistered"));
        //
        String url = "http://127.0.0.1/user/password_r?";
        //加密手机号
        url += "userId="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(email.getBytes("utf-8"),"szhl8888")));
        //拼接随机码
        url += "&salt="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(user.getSalt().getBytes("utf-8"),"szhl8888")));
        //拼接密码
        url += "&password="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(password.getBytes("utf-8"),"szhl8888")));
        //拼接时间
        url += "&date="+java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("utf-8"),"szhl8888")));
        //发送邮件
        String mayi = LoadFile.TemplateLoad(new File(modeurl + "mayi.html"));
        String s = mayi.replace("demo-title", MessageUtils.get("email.dome.title.3"))
                .replace("demo-text.1", MessageUtils.get("email.dome.text.1"))
                .replace("demo-text.2", MessageUtils.get("email.dome.text.2"))
                .replace("demo-text.3", MessageUtils.get("email.dome.text.a"))
                .replace("demo-text.url", url)
                .replace("demo-text.4", MessageUtils.get("email.dome.a.text"));
        log.info("邮箱发送"+url);
        emailservice.mail(user.getEmail(), s, MessageUtils.get("email.title.2"));
        return API.Success(url);

    }


    //修改密码邮箱验证方式
    public Json PassWordUpdate(String type,String password,String pass,String code) throws Exception {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
            User user = userDao.findByUserId(userId);
            if (user == null)
                return API.error(MessageUtils.get("user.unregistered"));
            //使用非对称加密密码
            String pas=MyMd5.Md5(password,user.getSalt(),1024);
            //验证密码是否正确
            if (!pas.equals(user.getPassword()))
                return API.error(MessageUtils.get("user.password.error"));
            if ("phone".equals(type)){
                if (user.getPhone() == null || "".equals(user.getPhone()))
                    return API.error(MessageUtils.get("user.update.type.no"));
                //手机号
                Session session = SecurityUtils.getSubject().getSession();
                String validateCode = (String) session.getAttribute("password_phone_code_update");
                if (validateCode == null)
                    return API.error(MessageUtils.get("verification.obtain"));
                if (code == null || "".equals(code)) {
                    return API.error( MessageUtils.get("verification.empty"));
                }
                if (!validateCode.equalsIgnoreCase(code)) {
                    return API.error(MessageUtils.get("verification.error"));
                }
                //
                String salts = MyMd5.Md5(mySalt.Salt(8),1024);
                //加密密码
                String pas1=MyMd5.Md5(password,salts,1024);
                user.setSalt(salts);
                user.setPassword(pas1);
                session.removeAttribute("password_phone_code_update");
                userDao.save(user);
                return API.Success(user);
            }else if ("email".equals(type)){
                if (user.getEmail() == null || "".equals(user.getEmail()))
                    return API.error(MessageUtils.get("user.update.type.no"));
                String url = "http://127.0.0.1/user/password_u?"
                        + "userId=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(user.getUserId().getBytes("utf-8"), "szhl8888")), "utf-8")
                        + "&salt=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(user.getSalt().getBytes("utf-8"), "szhl8888")), "utf-8")
                        + "&password=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(pass.getBytes("utf-8"), "szhl8888")), "utf-8")
                        + "&date=" + java.net.URLEncoder.encode(new BASE64Encoder().encode(des.encrypt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("utf-8"), "szhl8888")), "UTF-8");
                String mayi = LoadFile.TemplateLoad(new File(modeurl + "mayi.html"));
                String s = mayi.replace("demo-title", MessageUtils.get("email.dome.title.3"))
                        .replace("demo-text.1", MessageUtils.get("email.dome.text.1"))
                        .replace("demo-text.2", MessageUtils.get("email.dome.text.2"))
                        .replace("demo-text.3", MessageUtils.get("email.dome.text.a"))
                        .replace("demo-text.url", url)
                        .replace("demo-text.4", MessageUtils.get("email.dome.a.text"));
                log.info("邮箱发送"+url);
                emailservice.mail(user.getEmail(), s, MessageUtils.get("email.title.2"));
                return API.Success(MessageUtils.get("user.email.send"),url);
            }else {
                return API.error(MessageUtils.get("user.type.no"));
            }
    }

    /**
     *
     * @param password 旧密码
     * @param pass 新密码
     * @return
     * @throws Exception
     */
    public Json PassWordUpdate(String password,String pass) throws Exception {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userDao.findByUserId(userId);
        if (user == null)
            return API.error(MessageUtils.get("user.unregistered"));
        //使用非对称加密密码
        String pas=MyMd5.Md5(password,user.getSalt(),1024);
        //验证密码是否正确
        if (!pas.equals(user.getPassword()))
            return API.error(MessageUtils.get("user.password.error"));
        String salts = MyMd5.Md5(mySalt.Salt(8), 1024);
        //加密密码
        String pas1 = MyMd5.Md5(pass, salts, 1024);
        if (pas1.equals(user.getPassword()))
            return API.error("修改的密码与新密码相同");
        user.setSalt(salts);
        user.setPassword(pas1);
        userDao.save(user);
        //调用注销
        SecurityUtils.getSubject().logout();
        return API.Success("成功，下次登录将使用新密码！",user);
    }

    //修改密码和忘记密码连接验证
    public Json Password(String userId,String salt, String password,String date) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        if (date.equals(""))
            return API.error(MessageUtils.get("currency.parameter.error"));
        SimpleDateFormat simpleFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String toDate = simpleFormat.format(new Date());
        long from = simpleFormat.parse(new String(des.decrypt(decoder.decodeBuffer(date),"szhl8888"))).getTime();
        long to = simpleFormat.parse(toDate).getTime();
        int minutes = (int) ((to - from)/(1000 * 60));
        //找回密码
        if (!userId.equals("") && !password.equals("") && !salt.equals("")){
            //验证连接正确性
            //查询用户是否存在
            User user = userDao.findByUserId(new String(des.decrypt(decoder.decodeBuffer(userId),"szhl8888")));
            if (user == null)
                return API.error(MessageUtils.get("user.unregistered"));
            //验证SALT是否正确
            if (!user.getSalt().equals(new String(des.decrypt(decoder.decodeBuffer(salt),"szhl8888"))))
                return API.error(MessageUtils.get("user.url.modify"));
            //验证连接是否过期
            if (minutes >10)
                return API.error(MessageUtils.get("user.url.invalid"));
            //如果都通过，则开始加密密码，写入数据库
            //更新salt
            String salts = MyMd5.Md5(mySalt.Salt(8),1024);
            //加密密码
            String pas=MyMd5.Md5(new String(des.decrypt(decoder.decodeBuffer(password),"szhl8888")),salts,1024);
            //写入数据库
            user.setSalt(salts);
            user.setPassword(pas);
            userDao.save(user);
            return API.Success(MessageUtils.get("user.password.update.ok"));
        }
        return API.Success(MessageUtils.get("user.password.update.no"));
    }

    //用户修改钱包
    public Json Wallet(String type,String wallet){
        //获取手机号
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        //查询用户
        User user = userDao.findByUserId(userId);
        if (user == null)
            return API.error(MessageUtils.get("user.unregistered"));
        //更新钱包
        Map<String, String> map = new HashMap<>();
        map.put(type,wallet);
        user.setWallet(map);
        userDao.save(user);
        return API.Success(wallet);
    }


    //角色获取（用户）
    public Json UserRoles(){
        //获取用户
        String userid = (String) SecurityUtils.getSubject().getPrincipal();
        //查询角色
       List<Roles> roles = rolesDao.findByUserId(userid);
       if (roles == null)
        return API.Success();
       return API.Success(roles);
    }

    //用户头像上传
    public Json UserImg(MultipartFile file) throws Exception {
        return fileService.UserFile(file);
    }

    //用户实名接口
    public Json IDCardImg(MultipartFile fileA,MultipartFile fileB,MultipartFile fileC,String s,String name) throws Exception {
        return fileService.IDCardImg(fileA,fileB,fileC,s,name);
    }

    //以下是管理员方法

    //获取全部用户信息
    @RequiresRoles("admin")
    public Json UserAll(Integer index, Integer size, String id,Integer us){
        if (index == null){
            index = 0;
        }else {
            index = index - 1;
        }
        if (size == null || 0 == size){
            size = 10;
        }
        Pageable pageable = PageRequest.of(index,size);
        List<User> user = new ArrayList<>();
        if (id != null && !"".equals(id)){
            User u = userDao.findByUserId(id);
            List<Roles> roles = rolesDao.findByUserId(u.getUserId());
            List<String> userRoles = new ArrayList<>();
            for (Roles r:roles) {
                userRoles.add(r.getRole());
            }
            u.setRoles(userRoles);
            user.add(u);
        }else if (us != null ){
            Page<User> userPage = userDao.findByUserStart(us,pageable);
            if (userPage == null)
                return API.Success();
            for (User u:userPage) {
                List<Roles> roles = rolesDao.findByUserId(u.getUserId());
                List<String> userRoles = new ArrayList<>();
                for (Roles r:roles) {
                    userRoles.add(r.getRole());
                }
                u.setRoles(userRoles);
                user.add(u);
            }
        } else{
            Page<User> userPage = userDao.findAll(pageable);
            if (userPage == null)
                return API.Success();
            for (User u:userPage) {

                List<Roles> roles = rolesDao.findByUserId(u.getUserId());
                List<String> userRoles = new ArrayList<>();
                for (Roles r:roles) {
                    userRoles.add(r.getRole());
                }
                u.setRoles(userRoles);
                user.add(u);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("size",userDao.count());
        map.put("data",user);
        return API.Success(map);
    }

    //获取单个用户信息
    @RequiresRoles("admin")
    public Json UserOne(Integer id){
        Optional<User> user = userDao.findById(id);
        return API.Success(user.orElse(null));
    }

    //获取角色信息
    @RequiresRoles("admin")
    public Json RolesOne(String userid){
        return API.Success(rolesDao.findByUserId(userid));
    }

    //获取权限信息
    @RequiresRoles("admin")
    public Json RealmOne(List<Roles> roles){
        List<List<com.henglong.cloud.entity.Realm>> realm = new ArrayList<>();
        for (int i = 0;i < roles.size(); i++){
            realm.add(realmDao.findByRoles(roles.get(i).getRole()));
        }
        return API.Success(realm);
    }

    //更新角色信息
    @RequiresRoles("admin")
    public Json RolesUpdate(Roles roles){
//        if (userDao.findByUserId(roles.getUserId())!=null)
//            return API.Success(rolesDao.save(roles));
        if (roles.getId() == null) {
            rolesDao.save(roles);
            return API.Success(roles);
        }
        Optional<Roles> rolesOptional = rolesDao.findById(roles.getId());
        if (!rolesOptional.isPresent())
            return API.error("错误");
        Roles roles1 = rolesOptional.get();
        roles1.setId(roles.getId());
        roles1.setUserId(roles.getUserId());
        roles1.setRole(roles.getRole());
        rolesDao.save(roles1);
        return API.Success(roles1);
    }

    //添加角色信息
    @RequiresRoles("admin")
    public Json RolesInstall(Roles roles){
        return API.Success(rolesDao.save(roles));
    }

    //用户审核实名的方法
    public Json CardImg(String userId,Integer o){
        User user = userDao.findByUserId(userId);
        boolean b = false;
        if (o == 0)
            b = true;
        if (b){
            if (user == null)
                return API.error("未查询到用户！");
            user.setUserStart(0);
            userDao.save(user);
            return API.Success("成功，已通过");
        }else {
            user.setUserStart(4);
            userDao.save(user);
            return API.Success("成功，已拒绝");
        }
    }


    public Json sdfsdfsa(String password, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        User user = userDao.findByUserId("132254791418290176");
        String pass = MyMd5.Md5(password, user.getSalt(), 1024);
        //验证管理员密码
        if (user.getPassword().equals(pass)){
            //生成验证码
            response.setHeader("Cache-Control", "no-cache");
            String verifyCode = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 6, null);
            request.getSession().setAttribute("admin_phone_code", verifyCode);
            //发送验证码
            String s = HttpUtil.doGet("http://api.smsbao.com/sms?u=linlouyi&p=e8010d569268a39808ea940ea83d1501&m="+user.getPhone()+"&c="+ URLEncoder.encode("【比特中心】您正在重置系统最高管理权限登录IP,您的验证码是："+verifyCode+",此验证十分钟有效，如非本人操作，则密码可能泄露，请前往修改密码！", "UTF-8"));
            return API.Success(s);
        }
        return API.error("密码错误");
    }

    public Json sdfsda(String ipdsasd,String code){
        //验证短信验证码
        if (code == null || "".equals(code))
            return API.error(MessageUtils.get("verification.empty"));
        //获取正确的验证码
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("walletCode");
        if (validateCode != null) {
            code = code.toLowerCase();
            validateCode = validateCode.toLowerCase();
            if (!code.equals(validateCode)) {
                return API.error(MessageUtils.get("verification.error"));
            }
        }
        Config config = configDao.findById(1).get();
        config.setAmdinIp(ipdsasd);
        configDao.save(config);
        return API.Success();
    }

}
