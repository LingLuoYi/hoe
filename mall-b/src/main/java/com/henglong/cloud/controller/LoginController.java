package com.henglong.cloud.controller;

import com.henglong.cloud.config.shiro.NoPasswordToken;
import com.henglong.cloud.dao.ConfigDao;
import com.henglong.cloud.dao.UserDao;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.service.StaticService;
import com.henglong.cloud.util.*;
import com.henglong.cloud.util.aop.aopName.RequestLimit;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


@CrossOrigin(allowCredentials = "true")
@RestController
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private RedisTemplate<String, Session> redisTemplate;

//    private final StaticService staticService;

    private final UserDao userDao;

    private final ConfigDao configDao;

    @Autowired
    public LoginController(UserDao userDao, ConfigDao configDao) {
//        this.staticService = staticService;
        this.userDao = userDao;
        this.configDao = configDao;
    }


    //生成验证码图片
    @RequestMapping("/captcha/login_code.jpg")
    public void validateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        String verifyCode = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 6, null);
        request.getSession().setAttribute("LoginCode", verifyCode);
        log.info("验证码存储："+request.getSession().getId());
        response.setContentType("image/jpeg");
        BufferedImage bim = ValidateCode.generateImageCode(verifyCode, 135, 30, 10, true, Color.WHITE, Color.BLUE, null);
        ImageIO.write(bim, "JPEG", response.getOutputStream());
    }

    @GetMapping("/login")
    public Json GetLogin() {
        return API.Success( MessageUtils.get("login.get"));
    }

    @PostMapping("/login")
    @RequestLimit(count = 5)
    public Json PostLogin(User user1, ServletRequest request, String code, BindingResult bindingResult) {
        Json<Object> json = new Json<>();
        if (bindingResult.hasErrors()) {
            return API.login_no();
        }
        //获取正确的验证码
//        log.info("登录方法："+session.getId());
//        String validateCode = (String) session.getAttribute("LoginCode");
//        //校验
//        if (code == null || code.equals("")) {
//            return API.error(MessageUtils.get("verification.empty"));
//        }
//        if (validateCode == null)
//            return API.error( MessageUtils.get("verification.obtain"));
//        code = code.toLowerCase();
//        validateCode = validateCode.toLowerCase();
//        if (!code.equals(validateCode)) {
//            session.removeAttribute("LoginCode");
//            return API.error(MessageUtils.get("verification.error"));
//        }

        //获取登录名
        String password = user1.getPassword();
        String email = user1.getEmail();
        User user = new User();
        if (user1.getPhone() != null && !"".equals(user1.getPhone())) {
            user = userDao.findByPhone(user1.getPhone());
        }
        if (email != null && !"".equals(email)) {
            user = userDao.findByEmail(email);
        }
        if (user == null) {
            return API.error(MessageUtils.get("user.unregistered"));
        }
//        if ("132254791418290176".equals(user.getUserId())){
        if ("132254791418290176".equals(user.getUserId())){
            log.info(IPUtils.getRealIP((HttpServletRequest) request));
            if (!IPUtils.getRealIP((HttpServletRequest) request).equals(configDao.findById(1).get().getAmdinIp())){
                return API.error("当前用户不允许在此ip登录，如果ip变更，请重新更换ip");
            }
        }
        NoPasswordToken token = new NoPasswordToken(user.getUserId(), password);
        Subject currentUser = SecurityUtils.getSubject();
        try {
            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
            log.info("对用户[" + user.getUserId() + "]进行登录验证..验证开始");
            //防止重复登录
            currentUser.login(token);
//            staticService.Static();
            log.info("对用户[" + user.getUserId() + "]进行登录验证..验证通过");
        } catch (UnknownAccountException uae) {
            log.info("对用户[" + user.getUserId() + "]进行登录验证..验证未通过,未知账户");
            json.setState(CodeConstant.ERROR);
            json.setMsg(MessageUtils.get("user.unregistered"));
        } catch (IncorrectCredentialsException ice) {
//            ice.printStackTrace();
            log.info("对用户[" + user.getUserId() + "]进行登录验证..验证未通过,错误的凭证");
            json.setState(CodeConstant.MAT_ERR);
            json.setMsg(MessageUtils.get("login.password.error"));
        } catch (LockedAccountException lae) {
            log.info("对用户[" + user.getUserId() + "]进行登录验证..验证未通过,账户已锁定");
            json.setState(CodeConstant.LOCK);
            json.setMsg(MessageUtils.get("login.number.locking"));
        } catch (ExcessiveAttemptsException eae) {
            log.info("对用户[" + user.getUserId() + "]进行登录验证..验证未通过,错误次数过多");
            json.setState(CodeConstant.LOCK);
            json.setMsg(MessageUtils.get("login.frequency.error"));
        } catch (AuthenticationException ae) {
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
            log.info("对用户[" + user.getUserId() + "]进行登录验证..验证未通过,堆栈轨迹如下");
//            ae.printStackTrace();
            json.setState(CodeConstant.MAT_ERR);
            json.setMsg(MessageUtils.get("login.user.password.error"));
        }
        //验证是否登录成功
        if (currentUser.isAuthenticated()) {
            log.info("检查用户【"+user.getUserId()+"】，是否重复登录");
            for (Session sessionsID : getLoginedSession(currentUser)) {
                sessionsID.stop();
                redisTemplate.delete("CLOUD_"+sessionsID.getId());
                new EhCacheManager(redisTemplate).remove("CLOUD_"+sessionsID.getId());
                log.info("其他设备以踢下线！");
            }
            log.info("用户[" + user.getUserId() + "]登录认证通过");
            json.setState(CodeConstant.LOGIN_OK);
            json.setMsg(MessageUtils.get("login.ok"));
            Session session = SecurityUtils.getSubject().getSession();
            json.setData(session.getId());
            return json;
        } else {
            token.clear();
            return json;
        }
    }

    //获取登录验证码连接在此
    @RequestMapping("/get_login_code")
    public Json GetLoginCode(HttpServletRequest request, HttpServletResponse response, String code, String phone) {
        //生成短信验证码之前是不是要校验下图片验证码？
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("LoginCode");
        log.info("当前请求用户【"+phone+"】，图片验证码【"+validateCode+"】，用户输入的验证码【"+code+"】");
        //校验
        if (code == null || code.equals("")) {
            return API.error( MessageUtils.get("verification.empty"));
        }
        if (validateCode == null)
            return API.error( MessageUtils.get("verification.obtain"));
        code = code.toLowerCase();
        validateCode = validateCode.toLowerCase();
        if (!code.equals(validateCode)) {
            session.removeAttribute("LoginCode");
            return API.error( MessageUtils.get("verification.error"));
        }
        response.setHeader("Cache-Control", "no-cache");
        String verifyCode = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 8, null);
        request.getSession().setAttribute("LoginPhoneCode", verifyCode);
        //发送短信
        //                    log.info("短信发送记录："+HttpUtil.doPost(FileConfig.OutputPath("SMS-API","http://smssh1.253.com/msg/send/json"),
//                            FileConfig.getSubUtilSimple("SMS-content-code",
//                                    "{\"account\" : \"N8178610\",\"password\" : \"l2c7tWCnx\",\"msg\" : \"【蚂蚁区块链】您的登录验证码是：{code}，有效时间10分钟\", \"phone\" : \"{phone}\"}")
//                                    .replace("{code}",code)
//                                    .replace("{phone}",phone)));
        return API.Success(verifyCode);
    }

    @PostMapping("/login_sms")
    public Json PostMsgLogin(User user, HttpServletRequest request, String phoneCode, BindingResult bindingResult) {
        Json<String> json = new Json<>();
        if (bindingResult.hasErrors()) {
            return API.login_no();
        }
        //获取正确的验证码
        Session session = SecurityUtils.getSubject().getSession();
        String validateCode = (String) session.getAttribute("LoginPhoneCode");
        log.info("当前登录用户【" + user.getPhone() + "】，短信验证码为【" + validateCode + "】");
        //校验
        if (phoneCode == null || phoneCode.equals("")) {
            return API.error(MessageUtils.get("verification.empty"));
        }
        if (validateCode == null)
            return API.error(MessageUtils.get("verification.obtain"));
        phoneCode = phoneCode.toLowerCase();
        validateCode = validateCode.toLowerCase();
        if (!phoneCode.equals(validateCode)) {
            session.setAttribute("LoginPhoneCode","alkdgjgdjfsgisdrioegjjdfjksgheiwoarhghsdklfuhgera");
            return API.error(MessageUtils.get("verification.error"));
        }else if (validateCode.equals("alkdgjgdjfsgisdrioegjjdfjksgheiwoarhghsdklfuhgera")){
            return API.error(MessageUtils.get("verification.expire"));
        }
        //获取登录名
        String iphone = user.getPhone();
        user = userDao.findByPhone(iphone);
        log.info("这里是Controller,接受到用户" + iphone);
        NoPasswordToken token = new NoPasswordToken(user.getUserId());
        Subject currentUser = SecurityUtils.getSubject();
        try {
            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
            log.info("对用户[" + iphone + "]进行登录验证..验证开始");
            currentUser.login(token);
//            staticService.Static();
            log.info("对用户[" + iphone + "]进行登录验证..验证通过");
        } catch (UnknownAccountException uae) {
            log.info("对用户[" + iphone + "]进行登录验证..验证未通过,未知账户");
            json.setState(CodeConstant.ERROR);
            json.setMsg("不对，你还没有注册！");
        } catch (IncorrectCredentialsException ice) {
            log.info("对用户[" + iphone + "]进行登录验证..验证未通过,错误的凭证");
            json.setState(CodeConstant.MAT_ERR);
            json.setMsg("密码不正确");
        } catch (LockedAccountException lae) {
            log.info("对用户[" + iphone + "]进行登录验证..验证未通过,账户已锁定");
            json.setState(CodeConstant.LOCK);
            json.setMsg("账户已锁定");
        } catch (ExcessiveAttemptsException eae) {
            log.info("对用户[" + iphone + "]进行登录验证..验证未通过,错误次数过多");
            json.setState(CodeConstant.LOCK);
            json.setMsg("用户名或密码错误次数过多");
        }
        //验证是否登录成功
        if (currentUser.isAuthenticated()) {
            log.info("用户[" + iphone + "]登录认证通过");
            json.setState(CodeConstant.LOGIN_OK);
            json.setMsg("登录成功");
            session.removeAttribute("LoginPhoneCode");
            for (Session sessionsID : getLoginedSession(currentUser)) {
                sessionsID.stop();
                redisTemplate.delete("CLOUD_"+sessionsID.getId());
                new EhCacheManager(redisTemplate).remove("CLOUD_"+sessionsID.getId());
//                log.info("删除Session:CLOUD_"+sessionsID.getId());
                log.info("其他设备以踢下线！");
            }

            return json;
        } else {
            token.clear();
            session.removeAttribute("LoginPhoneCode");
            return json;
        }
    }

    private java.util.List<Session> getLoginedSession(Subject currentUser) {
//        Collection<Session> list = ((DefaultSessionManager) ((DefaultSecurityManager) SecurityUtils
//                .getSecurityManager()).getSessionManager()).getSessionDAO()
//                .getActiveSessions();
        Collection<String> list = redisTemplate.keys("CLOUD_*");
        java.util.List<Session> loginedList = new ArrayList<Session>();
        String loginUser = (String) currentUser.getPrincipal();
        assert list != null;
        for (String se : list) {
            Subject s = new Subject.Builder().session(redisTemplate.opsForValue().get(se)).buildSubject();
            if (s.isAuthenticated()) {
                String user = (String) s.getPrincipal();
                if (user.equalsIgnoreCase(loginUser)) {
                    if (!(redisTemplate.opsForValue().get(se)).getId().equals(
                            currentUser.getSession().getId())) {
                        loginedList.add(redisTemplate.opsForValue().get(se));
                    }
                }
            }
        }
        return loginedList;
    }

}
