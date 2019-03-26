package com.henglong.cloud.config.filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


public class CaptchaValidateFilter extends AccessControlFilter {

    //接受验证码的参数
    private String captchaParam = "captchaCode";

    //存储验证码信息
    private String failureKeyAttribute = "shiroLoginFailure";

    public String getCaptchaCode(ServletRequest request) {
        return WebUtils.getCleanParam(request, getCaptchaParam());
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {

        //获取正确的验证码
        Session session = SecurityUtils.getSubject().getSession();
        //获取输入的验证码
        String captchaCode = getCaptchaCode(servletRequest);
        String validateCode = (String)session.getAttribute(failureKeyAttribute);
        HttpServletRequest httpServletRequest = WebUtils.toHttp(servletRequest);

        //判断验证码是否表单提交（允许访问）
        if ( !"post".equalsIgnoreCase(httpServletRequest.getMethod())) {
            return true;
        }
        // 若验证码为空或匹配失败则返回false
        if(captchaCode == null) {
            return false;
        } else if (validateCode != null) {
            captchaCode = captchaCode.toLowerCase();
            validateCode = validateCode.toLowerCase();
            if(!captchaCode.equals(validateCode)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        //如果验证码失败了，存储失败key属性
        servletRequest.setAttribute(failureKeyAttribute, "验证码错误");
        return true;
    }

    public String getCaptchaParam() {
        return captchaParam;
    }

    public void setCaptchaParam(String captchaParam) {
        this.captchaParam = captchaParam;
    }
}
