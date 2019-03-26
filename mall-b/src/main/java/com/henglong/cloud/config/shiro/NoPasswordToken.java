package com.henglong.cloud.config.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class NoPasswordToken extends UsernamePasswordToken {

    private static final long serialVersionUID = -2564928913725078138L;

    private Integer loginType;


    /**
     * 账号密码登录
     * @param username
     * @param password
     */
    public NoPasswordToken(String username, String password) {
        super(username,password);

        this.loginType=0;
    }

    /**
     * 免密登录
     */
    public NoPasswordToken(String username) {
        super(username,"");

        this.loginType=1;
    }

    public Integer getLoginType() {
        return loginType;
    }
}

