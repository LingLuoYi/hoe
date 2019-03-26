package com.henglong.cloud.config.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    Logger log = LoggerFactory.getLogger(RetryLimitHashedCredentialsMatcher.class);

    @Autowired
    private RedisTemplate redisTemplate;

    public RetryLimitHashedCredentialsMatcher() {

    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

        NoPasswordToken noPasswordToken = (NoPasswordToken) token;
        //如果是免密，就不需要核对密码了
        if (noPasswordToken.getLoginType() == 1) {
            return true;
        }
        String username = (String) token.getPrincipal();
        //retry count + 1
        Integer retryCount = 0;
        try {
            retryCount =  (Integer) redisTemplate.opsForValue().get(username);
        }catch (Exception e){
            log.info("错误!");
        }finally {
            Date now = new Date();

            if ((Integer)redisTemplate.opsForValue().get(username) == null) {
                redisTemplate.opsForValue().set(username, 0,1800000, TimeUnit.MILLISECONDS);
            } else if ((Integer)redisTemplate.opsForValue().get(username) > 5) {
                throw new ExcessiveAttemptsException("密码填写错误5次,账号已被锁定，请30分钟后再登录");
            }

            boolean matches = super.doCredentialsMatch(token, info);
            if (matches) {
                //clear retry count
                redisTemplate.opsForValue().getOperations().delete(username);
            } else {
                Integer i = ((Integer) redisTemplate.opsForValue().get(username));
                log.info("当前尝试次数【"+redisTemplate.opsForValue().get(username)+"】");
                Integer j = i+1;
                redisTemplate.opsForValue().set(username, j,1800000, TimeUnit.MILLISECONDS);
            }
            return matches;
        }
    }
}
