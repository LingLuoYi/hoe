package com.henglong.cloud.controller;


import com.henglong.cloud.dao.UserDao;
import com.henglong.cloud.entity.Address;
import com.henglong.cloud.entity.Roles;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.service.UserService;
import com.henglong.cloud.util.*;
import com.henglong.cloud.util.aop.aopName.RequestLimit;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/user")
public class UserInfoController  {

    private static final Logger log = LoggerFactory.getLogger(UserInfoController.class);

    private final UserService userService;

    private final UserDao userDao;

    @Autowired
    public UserInfoController(UserService userService, UserDao userDao) {
        this.userService = userService;
        this.userDao = userDao;
    }

    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/user_info")
    public Json UserInfo(){
        return userService.UserInfo();
    }

    //添加收货地址
    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @RequestMapping("/user_address_add")
    public Json UserAddress(Address address){
        return userService.UserAddress(address);
    }

    //修改收货地址
    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @RequestMapping("/user_address_update")
    public Json UserUpdateAddress(Address address){
        return userService.UserUpdateAddress(address);
    }

    @RequiresPermissions(value = {"user:delete","admin:delete"},logical = Logical.OR)
    @RequestMapping("/user_address_delete")
    public Json UserDeleteAddress(Integer id){
        return userService.UserDeleteAddress(id);
    }

    @RequiresPermissions(value = {"user:update","admin:select"},logical = Logical.OR)
    @PostMapping("/user_update")
    public Json PostUser_Add(@Valid User user){
        return userService.UserAdd(user);
    }

    @RequiresPermissions("admin:update")
    @PostMapping("/roles_update")
    public Json PostRoles(Roles role){
        return userService.RolesUpdate(role);
    }

    /*邮件确认连接*/
    @RequestMapping("/email_user_confirm")
    public String EmailConfirm(@RequestParam("userId") String phone,@RequestParam("email") String email,@RequestParam("date") String date ,@RequestParam("code") String code) throws Exception {
        userService.MailConfirm(phone,email,date,code);
        return "redirect:/login";
    }

    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @RequestMapping("/email")
    public Json mail(@RequestParam("email") String email) throws Exception {
//        if (!Regular.isEmail(email)){
//            return API.error("邮箱格式不正确");
//        }
//        userService.mail(email);
//        return API.Success(MessageUtils.get("currency.success"));
        return API.error("接口废弃");
    }

    //图片验证码
    @RequestMapping("/captcha/password_code.jpg")
    public void PasswordCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        String password_code = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 6, null);
        request.getSession().setAttribute("password_code", password_code);
        response.setContentType("image/jpeg");
        BufferedImage bim = ValidateCode.generateImageCode(password_code, 135, 30, 10, true, Color.WHITE, Color.BLUE, null);
        ImageIO.write(bim, "JPEG", response.getOutputStream());
    }

    //发送手机验证码
    @RequestLimit(count = 1)
    @RequestMapping("/passwordCode")
    public Json passwordPhoneCode(String email,String phone,String code,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
        //验证图片验证码
//        Session session = SecurityUtils.getSubject().getSession();
//        String validateCode = (String) session.getAttribute("password_code");
//        if (code == null || code.equals("")) {
//            return API.error(MessageUtils.get("verification.empty"));
//        }
//        if (validateCode != null) {
//            code = code.toLowerCase();
//            validateCode = validateCode.toLowerCase();
//            if (!code.equals(validateCode)) {
//                session.removeAttribute("password_code");
//                return API.error( MessageUtils.get("verification.error"));
//            }
//        }
        return userService.passwordPhoneCode(phone,email,request,response);
    }

    //绑定手机
    @RequestMapping("/binding")
    public Json binding(String phone,String email, String code) throws UnsupportedEncodingException {
        return userService.bindingPhone(phone,email,code);
    }


    //更改绑定接受连接
    @GetMapping("/binding_phone")
    public Json binding(String userId,String phone,String date,String code) throws Exception {
        return userService.binding(userId,phone,date,code);
    }

    //忘记密码发送验证码
    @RequestLimit(count = 1)
    @RequestMapping("/forget_password")
    public Json passwordCode(String email,String phone,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
        return userService.passwordCode(phone,email,request,response);
    }


    //忘记密码
    @RequestMapping("/password_retrieve_phone")
    public Json RetrievePassWord(String email, String phone ,@RequestParam("password")String password,@RequestParam("code")String code) throws Exception {
        return userService.RetrievePassWord(email,phone,password,code);
    }

    @RequestMapping("/password_retrieve_email")
    public Json RetrievePassWord(@RequestParam("email") String email,@RequestParam("password") String password,@RequestParam("code")String code) throws Exception {
        return userService.RetrievePassWord(email,null,password,code);
    }

    //修改密码
    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @RequestMapping("/password_update")
    public Json PasswordUpdate(@RequestParam("password")String password,@RequestParam("pass")String pass) throws Exception {
        return userService.PassWordUpdate(password,pass);
    }

    //接受连接
    @RequestMapping("/password_r")
    public Json PasswordR(@RequestParam("phone")String phone,@RequestParam("password")String password,@RequestParam("salt")String salt,@RequestParam("date")String date) throws Exception {
        return userService.Password(phone,salt,password,date);
    }

    //接受连接
    @RequestMapping("/password_u")
    public Json PassWordUpdate(@RequestParam("phone")String phone,@RequestParam("salt")String salt,@RequestParam("password")String password,@RequestParam("date")String date) throws Exception {
        return userService.Password(phone,salt,password,date);
    }

    //角色获取连接
    @RequiresPermissions(value = {"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("/user_roles")
    public Json UserRoles(){
        return userService.UserRoles();
    }

    //用户头像上传
    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @RequestMapping("/user_img")
    public Json UserImg(@RequestParam("file")MultipartFile file) throws Exception {
        return userService.UserImg(file);
    }

    //用户头像显示
    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @RequestMapping("/user_img/{name}")
    public void UserImgInfo(HttpServletResponse response, @PathVariable("name")String name){
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(LoadFile.Path()+"/img/user/"+name);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.warn("显示图片发生了异常");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.warn("图片显示类，关闭流出现了异常",e);
                }
            }
        }
    }
    //用户实名认证
    @PostMapping("/card_img")
    public Json cardImgs(MultipartFile fileA,MultipartFile fileB,MultipartFile fileC,String s,String name) throws Exception {
        return userService.IDCardImg(fileA,fileB,fileC,s,name);
    }


    //用户身份证显示
    @RequiresPermissions(value = {"admin:select","finance:select","cashier:select"},logical = Logical.OR)
    @RequestMapping("/card_img/{card}/{name}")
    public void cardImg(HttpServletResponse response,@PathVariable("card") String card,@PathVariable("name") String name){
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(LoadFile.Path()+"/img/user/"+card+"/"+name);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.error("显示图片发生了异常");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("图片显示类，关闭流出现了异常",e);
                }
            }
        }
    }

    //图片验证码
    @RequestMapping("/captcha/wallet_code.jpg")
    public void validateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        String SinUpCode = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 6, null);
        request.getSession().setAttribute("wallet_code", SinUpCode);
        response.setContentType("image/jpeg");
        BufferedImage bim = ValidateCode.generateImageCode(SinUpCode, 135, 30, 10, true, Color.WHITE, Color.BLUE, null);
        ImageIO.write(bim, "JPEG", response.getOutputStream());
    }

    //发送短信验证码
    @RequestLimit(count = 1)
    @RequestMapping("/captcha/wallet")
    public Json WalletCode( HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Cache-Control", "no-cache");
        String walletCode = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_ONLY, 6, null);
        request.getSession().setAttribute("walletCode", walletCode);
        //验证图片验证码
//        Session session = SecurityUtils.getSubject().getSession();
//        String validateCode = (String) session.getAttribute("wallet_code");
//        if (code == null || code.equals("")) {
//            return API.error(MessageUtils.get("verification.empty"));
//        }
//        if (validateCode != null) {
//            code = code.toLowerCase();
//            validateCode = validateCode.toLowerCase();
//            if (!code.equals(validateCode)) {
//                session.removeAttribute("wallet_code");
//                return API.error( MessageUtils.get("verification.error"));
//            }
//        }
        //短信发送
        log.info("【"+SecurityUtils.getSubject().getPrincipal()+"】的短信验证码： "+walletCode);
        String phone = userDao.findByUserId((String)SecurityUtils.getSubject().getPrincipal()).getPhone();
        if (phone != null && !"".equals(phone)) {
            return API.Success(notice.sms(userDao.findByUserId((String) SecurityUtils.getSubject().getPrincipal()).getPhone(), "139490", walletCode));
        }else {
            return API.error("请先绑定手机");
        }
    }

    //用户添加钱包修改钱包
    @RequestMapping("/wallet")
    public Json Wallet(@RequestParam("type")String type,@RequestParam("wallet")String wallet,@RequestParam("code")String code){
        ////////////////////////////
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
        if (wallet.length() > 30)
        ////////////////////////
        return userService.Wallet(type,wallet);
        else
            return API.error("地址不正确");
    }

    @RequiresPermissions(value = {"admin:install","finance:install"})
    @PostMapping("/card_s")
    public Json CardS(String userId,Integer o){
        return userService.CardImg(userId,o);
    }

    @RequiresPermissions("admin:select")
    @PostMapping("/roles_one")
    public Json PostrolesInfo(@RequestParam("phone") String phone){
        return userService.RolesOne(phone);
    }

    @RequiresPermissions("admin:select")
    @PostMapping("/realm_one")
    public Json PostRealmInfo(@Valid List<Roles> roles){
        return userService.RealmOne(roles);
    }

    @RequiresPermissions("admin:select")
    @RequestMapping("/user_all_data")
    public Json UserAll(Integer page, Integer limit,String id,Integer userStart){
        return userService.UserAll(page,limit,id,userStart);
    }

    @RequiresPermissions(value = {"admin:install","admin:update"},logical = Logical.AND)
    @PostMapping(value = "/user_admin_add")
    public Json UserAdd(@Valid User user){
        return userService.UserAdminAdd(user);
    }


}
