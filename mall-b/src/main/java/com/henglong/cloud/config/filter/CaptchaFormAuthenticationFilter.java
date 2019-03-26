package com.henglong.cloud.config.filter;


import com.henglong.cloud.util.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class CaptchaFormAuthenticationFilter extends FormAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(CaptchaFormAuthenticationFilter.class);

//    @Override
//    protected boolean onLoginSuccess(AuthenticationToken token,
//                                     Subject subject, ServletRequest request, ServletResponse response)
//            throws Exception {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//
//        if (!"XMLHttpRequest".equalsIgnoreCase(httpServletRequest
//                .getHeader("X-Requested-With"))) {// 不是ajax请求
//            issueSuccessRedirect(request, response);
//            log.info("登录成功");
//        } else {
//            httpServletResponse.setCharacterEncoding("UTF-8");
//            PrintWriter out = httpServletResponse.getWriter();
//            out.println("{success:true,message:'登入成功'}");
//            out.flush();
//            out.close();
//        }
//        return false;
//    }
//    @Override
//    protected boolean onLoginFailure(AuthenticationToken token,
//                                     AuthenticationException e, ServletRequest request,
//                                     ServletResponse response) {
//        if (!"XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request)
//                .getHeader("X-Requested-With"))) {// 不是ajax请求
//            setFailureAttribute(request, e);
//            log.info("登录拦截");
//            return true;
//        }
//        try {
//            response.setCharacterEncoding("UTF-8");
//            PrintWriter out = response.getWriter();
//            String message = e.getClass().getSimpleName();
//            if ("IncorrectCredentialsException".equals(message)) {
//                out.println("{success:false,message:'密码错误'}");
//            } else if ("UnknownAccountException".equals(message)) {
//                out.println("{success:false,message:'账号不存在'}");
//            } else if ("LockedAccountException".equals(message)) {
//                out.println("{success:false,message:'账号被锁定'}");
//            } else {
//                out.println("{success:false,message:'未知错误'}");
//            }
//            out.flush();
//            out.close();
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        return false;
//    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.info("检测到登录提交。尝试执行登录。");
                }
                return executeLogin(request, response);
            } else {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.println("{\"state\":1,\"msg\":\"没有登录！→_→\"}");
                out.flush();
                out.close();
                return false;
            }
        } else {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.println("{\"state\":1,\"msg\":\"没有登录！→_→\"}");
                out.flush();
                out.close();
            return false;
        }
    }
}
