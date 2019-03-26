package com.henglong.cloud.controller;

import com.henglong.cloud.entity.User;
import com.henglong.cloud.service.EmailService;
import com.henglong.cloud.service.SignUpService;
import com.henglong.cloud.util.*;
import com.henglong.cloud.util.aop.aopName.RequestLimit;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 等有时间把这里的逻辑方法移到Server
 */
@CrossOrigin(allowCredentials="true")
@RestController
public class SignupController {

    private final static Logger log = LoggerFactory.getLogger(SignupController.class);

    private static final String modeurl = LoadFile.Path()+"/mode/";

    private final SignUpService signUpService;

    private final EmailService emailService;

    @Autowired
    public SignupController(SignUpService signUpService, EmailService emailService) {
        this.signUpService = signUpService;
        this.emailService = emailService;
    }


    //获取验证码普通图片
//    @RequestMapping("/captcha/sig_up_code.jpg")
    public void validateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        String SinUpCode = ValidateCode.generateTextCode(ValidateCode.TYPE_ALL_MIXED, 6, null);
        request.getSession().setAttribute("SigUpCode", SinUpCode);
        response.setContentType("image/jpeg");
        BufferedImage bim = ValidateCode.generateImageCode(SinUpCode, 135, 30, 10, true, Color.WHITE, Color.BLUE, null);
        ImageIO.write(bim, "JPEG", response.getOutputStream());
    }

    //获取短信验证码
//    @RequestLimit(count = 1)
//    @RequestMapping("/captcha/phone")
    public Json PhoneCode(String phone,String code,HttpServletRequest request, HttpServletResponse response)throws Exception {
        if (!Regular.isPhone(phone)){
            return API.error("手机号格式不正确");
        }
        response.setHeader("Cache-Control", "no-cache");
        String PhoneCode = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_ONLY, 6, null);
        request.getSession().setAttribute("PhoneCode", PhoneCode);
        //验证图片验证码
//        Session session = SecurityUtils.getSubject().getSession();
//        String validateCode = (String) session.getAttribute("SigUpCode");
//        if (code == null || code.equals("")) {
//            return API.error(CodeConstant.ERROR, MessageUtils.get("verification.empty"));
//        }
//        if (validateCode == null)
//            return API.error(CodeConstant.ERROR,MessageUtils.get("verification.obtain"));
//        code = code.toLowerCase();
//        validateCode = validateCode.toLowerCase();
//        if (!code.equals(validateCode)) {
//            session.removeAttribute("SigUpCode");
//            return API.error(CodeConstant.ERROR, MessageUtils.get("verification.error"));
//        }
        log.info("用户【"+phone+"】短信验证码是："+PhoneCode);
        log.info("短信发送记录："+notice.sms(phone,"",PhoneCode));
        return API.Success();
    }
    //获取邮箱验证码
//    @RequestLimit(count = 1)
//    @RequestMapping("/captcha/email")
    public Json EmailCode(String email, String code,HttpServletRequest request,HttpServletResponse response){
        if (!Regular.isEmail(email)){
            return API.error("邮箱格式不正确");
        }
        //验证图片验证码
//        Session session = SecurityUtils.getSubject().getSession();
//        String validateCode = (String) session.getAttribute("PhoneCode");
//        if (code == null || code.equals("")) {
//            return API.error(CodeConstant.ERROR, MessageUtils.get("verification.empty"));
//        }
//        if (validateCode == null)
//            return API.error(CodeConstant.ERROR,MessageUtils.get("verification.obtain"));
//        code = code.toLowerCase();
//        validateCode = validateCode.toLowerCase();
//        if (!code.equals(validateCode)) {
//            session.removeAttribute("SigUpCode");
//            return API.error(CodeConstant.ERROR, MessageUtils.get("verification.error"));
//        }
        response.setHeader("Cache-Control", "no-cache");
        String PhoneCode = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_ONLY, 6, null);
        request.getSession().setAttribute("PhoneCode", PhoneCode);
        //////////拼接邮件类容
//        String mayi = LoadFile.TemplateLoad(new File(modeurl + "mayi2.html"));
//        String s = mayi.replace("demo-title", MessageUtils.get("email.dome.title"))
//                .replace("demo-text.1", MessageUtils.get("email.dome.text.1"))
//                .replace("demo-text.2", MessageUtils.get("email.dome.text.2"))
//                .replace("demo-text.3", MessageUtils.get("email.dome.text.3.signup"))
//                .replace("demo-text.4", PhoneCode);
//        log.info("{}",PhoneCode);
        emailService.mail(email,"您的验证码是："+PhoneCode,MessageUtils.get("email.dome.title"));
//        session.removeAttribute("PhoneCode");
        return API.Success();
    }

    @RequestMapping("/sign_up")
    public Json GetSignup(){
        return API.error("暂不提供注册，如需购买请与我们联系！");
    }

    @RequestMapping("/captcha/email")
    public Json sd(){
        return API.error("暂不提供注册，如需购买请与我们联系！");
    }
    @RequestMapping("/captcha/phone")
    public Json sds(){
        return API.error("暂不提供注册，如需购买请与我们联系！");
    }

//    @PostMapping("/sign_up/{code}")
    public Json PostSignup(@Valid User user,@PathVariable(value = "code",required = false)String code,@RequestParam("code")String phoneCode){
        if (user.getPhone() != null && !"".equals(user.getPhone())) {
            //验证短信验证码
            Session session = SecurityUtils.getSubject().getSession();
            String codes = (String) session.getAttribute("PhoneCode");
            if (phoneCode == null) {
                return API.error(MessageUtils.get("verification.obtain"));
            } else {
                codes = codes.toLowerCase();
                phoneCode = phoneCode.toLowerCase();
                if (!codes.equals(phoneCode)) {
                    session.removeAttribute("PhoneCode");
                    return API.error(MessageUtils.get("verification.error"));
                }
            }
            log.info("有人注册啦！【" + user.getPhone() + "】");
            return signUpService.SignUp(user, code);
        }else if (user.getEmail() != null && !"".equals(user.getEmail())){
            //验证邮箱验证码
            Session session = SecurityUtils.getSubject().getSession();
            String codes = (String) session.getAttribute("PhoneCode");
            if (phoneCode == null) {
                return API.error(MessageUtils.get("verification.obtain"));
            } else {
                codes = codes.toLowerCase();
                phoneCode = phoneCode.toLowerCase();
                if (!codes.equals(phoneCode)) {
                    return API.error(MessageUtils.get("verification.error"));
                }
            }
            return signUpService.SignUp(user,code);
        }else {
            return API.error(MessageUtils.get("signup.no.phone"));
        }
    }

//    @PostMapping("/sign_up")
    public Json PostSignup(@Valid User user,@RequestParam("code")String phoneCode,HttpServletRequest request, HttpServletResponse response) {
        if (user.getPhone() != null && !"".equals(user.getPhone())) {
            //验证短信验证码
            Session session = SecurityUtils.getSubject().getSession();
            String codes = (String) session.getAttribute("PhoneCode");
            if (codes == null)
                return API.error("请先获取验证码");
            if (phoneCode == null) {
                return API.error(MessageUtils.get("verification.obtain"));
            } else {
                codes = codes.toLowerCase();
                phoneCode = phoneCode.toLowerCase();
                if (!codes.equals(phoneCode)) {
                    session.removeAttribute("PhoneCode");
                    return API.error(MessageUtils.get("verification.error"));
                }
            }
            log.info("有人注册啦！【" + user.getPhone() + "】");
            return signUpService.SignUp(user, "");
        } else if (user.getEmail() != null && !"".equals(user.getEmail())) {
            //验证邮箱验证码
            Session session = SecurityUtils.getSubject().getSession();
            String codes = (String) session.getAttribute("PhoneCode");
            if (phoneCode == null) {
                return API.error(MessageUtils.get("verification.obtain"));
            } else {
                codes = codes.toLowerCase();
                phoneCode = phoneCode.toLowerCase();
                if (!codes.equals(phoneCode)) {
                    return API.error(MessageUtils.get("verification.error"));
                }
            }
            return signUpService.SignUp(user, "");
        } else {
            return API.error(MessageUtils.get("signup.no.phone"));
        }
    }
}
