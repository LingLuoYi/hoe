package com.henglong.cloud.Exception;

import com.henglong.cloud.util.API;
import com.henglong.cloud.util.CodeConstant;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.MessageUtils;
import com.sun.mail.smtp.SMTPSendFailedException;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.authc.AccountException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.util.NestedServletException;

import javax.mail.SendFailedException;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;

@ControllerAdvice
public class WordException extends RuntimeException {

    private final static Logger log = LoggerFactory.getLogger(WordException.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Json AntiEvil(Exception e) throws Exception{
        if (e instanceof TransactionSystemException){
            throw new TransactionSystemException(MessageUtils.get("currency.data"));
        }else if (e instanceof NullPointerException){
            log.warn("空指针异常");
            return API.error(CodeConstant.ERR_SYSTEM,"空指针异常");
        }else if (e instanceof NumberFormatException){
            log.warn("数字格式异常");
            return API.error(CodeConstant.ERR_PAR,"数字格式异常");
        }else if (e instanceof MissingServletRequestParameterException){
            return API.error(CodeConstant.ERR_PAR,"没有提供所需要的字段");
        }else if (e instanceof IOException){
            log.info("系统IO异常："+e.getMessage());
            return API.error(CodeConstant.NO_AUTH,"系统IO异常:"+e.getMessage());
        }else if (e instanceof SendFailedException){
            log.warn("{}",e.getMessage());
            return API.error("无效信息："+e.getMessage());
        }else if (e instanceof MultipartException){
            log.warn("文件上传异常：{}",e.getMessage());
            return API.error("文件上传异常："+e.getMessage());
        }else if (e instanceof SQLException){
            log.warn("{}",e.getMessage());
            return API.error("数据库错误："+e.getMessage());
        }else if (e instanceof MethodArgumentTypeMismatchException){
            log.warn("类型转换异常：{}",e.getMessage());
            return API.error("请提供正确的参数类型");
        }else if (e instanceof DataIntegrityViolationException){
            log.warn("数据库数据重复");
            return API.error("数据重复");
        }else if (e instanceof SMTPSendFailedException){
            log.warn("邮件发送失败");
            return API.error("邮件发生失败："+e.getMessage());
        }else if (e instanceof RequestLimitException){
            log.warn("用户频繁请求接口");
            return API.error("请求频率过快");
        }else if (e instanceof NestedServletException){
            log.error("系统运行发生了异常");
            return API.error(CodeConstant.ERR_SYSTEM,"系统发生了错误");
        }else if (e instanceof UndeclaredThrowableException){
            log.warn("未定义异常，可能是访问太频繁");
            return API.error("未定义异常，可能是访问太频繁");
        }

        String msg = e.getMessage();
        if (msg != null) {
            if (msg.contains("Subject does not have permission")) {
                log.warn("用户【" + (String) SecurityUtils.getSubject().getPrincipal() + "】请求了一个超越自己权限的连接");
                return API.error(CodeConstant.NO_AUTH, MessageUtils.get("index.power"));
            } else if (msg.contains("Required String parameter")) {
                log.warn("错误的接口调用");
                return API.error(MessageUtils.get("currency.interface.error"));
            } else if (msg.contains("Request method")) {
                log.warn("错误的请求方式");
                return API.error(CodeConstant.ERR_SYSTEM, MessageUtils.get("currency.mode"));
            }
        }

//        log.error("程序运行出现了一次未知异常："+e.getMessage());
        System.out.println(e);
//        throw e;
        return API.error(CodeConstant.ERR_SYSTEM,"未知异常:"+e.getMessage());
    }

    @ExceptionHandler(value = AccountException.class)
    @ResponseBody
    public Json handleShiroException(Exception ex) {
        log.error("发生了一次异常，具体信息为如下",ex);
        return API.error(CodeConstant.ERR_SYSTEM,ex.getMessage());
    }
}
