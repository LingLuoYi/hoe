package com.henglong.cloud.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.henglong.cloud.config.filter.CaptchaFormAuthenticationFilter;
import com.henglong.cloud.config.filter.CaptchaValidateFilter;
import com.henglong.cloud.config.redis.RedisCacheManager;
import com.henglong.cloud.config.redis.RedisSessionDao;
import com.henglong.cloud.config.shiro.*;
import com.henglong.cloud.dao.ConfigDao;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ShiroConfiguration.class);

    @Autowired
    private ConfigDao configDao;


    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        log.info("注入Shiro的Web过滤器-->shiroFilter", ShiroFilterFactoryBean.class);
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();


        //Shiro的核心安全接口,这个属性是必须的
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        /*定义shiro过滤链  Map结构
         * Map中key(xml中是指value值)的第一个'/'代表的路径是相对于HttpServletRequest.getContextPath()的值来的
         * anon：它对应的过滤器里面是空的,什么都没做,这里.do和.jsp后面的*表示参数,比方说login.jsp?main这种
         * authc：该过滤器下的页面必须验证后才能访问,它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
         */
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/logout", "logout");

        // <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        //不会被拦截的连接
        filterChainDefinitionMap.put("/webui/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/","anon");
        filterChainDefinitionMap.put("/sign_up/**","anon");
        filterChainDefinitionMap.put("/user/email_user_confirm/**","anon");
        filterChainDefinitionMap.put("/user/password_retrieve/**","anon");
        filterChainDefinitionMap.put("/user/password_r/**","anon");
        filterChainDefinitionMap.put("/user/password_u/**","anon");
        filterChainDefinitionMap.put("/commodity/commodity_info_all","anon");
        filterChainDefinitionMap.put("/commodity/commodity_info_id","anon");
        filterChainDefinitionMap.put("/commodity/commodity_info_name","anon");
        filterChainDefinitionMap.put("/commodity/commodity_info_type","anon");
        filterChainDefinitionMap.put("/commodity/commodity_info_page","anon");
        filterChainDefinitionMap.put("/captcha/**","anon");
        filterChainDefinitionMap.put("/get_login_code","anon");
        filterChainDefinitionMap.put("/login_msg","anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/s", "anon");
        filterChainDefinitionMap.put("/s/**", "anon");
        filterChainDefinitionMap.put("/assets/sdsdfsdfsdasdfasfsadfsadfsdfwewrwrtrqu/werterrweqcxvx/sewrsdfw","anon");//支付宝通知连接
        filterChainDefinitionMap.put("/assets/sdfjsaksjadghushfnxcsdfksdjafhusahdf/sfsdfasifhx/qwexfdse","anon");//微信回调连接
        filterChainDefinitionMap.put("/menu/menu_title","anon");
        filterChainDefinitionMap.put("/menu/menu_all","anon");
        filterChainDefinitionMap.put("/user/binding_phone","anon");//换绑手机号接受连接
        filterChainDefinitionMap.put("/user/forget_password","anon");
        filterChainDefinitionMap.put("/user/password_retrieve_phone","anon");
        filterChainDefinitionMap.put("/user/password_retrieve_email","anon");
        filterChainDefinitionMap.put("/image/company/**","anon");
        filterChainDefinitionMap.put("/admin/login/**","anon");
        filterChainDefinitionMap.put("/words/**","anon");
        filterChainDefinitionMap.put("/hoe/transformation","anon");
        filterChainDefinitionMap.put("/pool_info","anon");
        filterChainDefinitionMap.put("/admin_phone_code","anon");
        filterChainDefinitionMap.put("/admin_ip_update","anon");
        //需要权限的连接
        filterChainDefinitionMap.put("/**", "user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        //要求登录时的链接(可根据项目的URL进行替换),非必须的属性,默认会自动寻找Web工程根目录下的"/login.jsp"页面
//        shiroFilterFactoryBean.setLoginUrl("/login");
        //登录成功后要跳转的连接,逻辑也可以自定义，例如返回上次请求的页面
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //用户访问未对其授权的资源时,所显示的连接
        shiroFilterFactoryBean.setUnauthorizedUrl("/error/403");


        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("captchaVaildate", new CaptchaValidateFilter());

        return shiroFilterFactoryBean;
    }

    /**
     * Shiro Realm 继承自AuthorizingRealm的自定义Realm,即指定Shiro验证用户登录的类为自定义的
     *
     * @return
     */
    @Bean
    public RealmConfig userRealm() {
        RealmConfig userRealm = new RealmConfig();
        //告诉realm,使用credentialsMatcher加密算法类来验证密文
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        userRealm.setCachingEnabled(false);
        return userRealm;
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     *  所以我们需要修改下doGetAuthenticationInfo中的代码;
     *
     * @return
     */
    @Bean(name="credentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
//        Config config = configDao.findById(1).get();
        RetryLimitHashedCredentialsMatcher hashedCredentialsMatcher = new RetryLimitHashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(1024);//散列的次数，比如散列两次，相当于 md5(md5(""));
        //storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);

        return hashedCredentialsMatcher;
    }


    /**
     * Shiro生命周期处理器
     * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }
    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * @return
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    //会话管理
    @Bean(name="sessionManager")
    public DefaultWebSessionManager configWebSessionManager(){
        ShiroSessionManager manager = new ShiroSessionManager();
        manager.setSessionValidationInterval(redisSessionDao().getExpireTime());
        manager.setSessionValidationSchedulerEnabled(true);
        manager.setSessionIdUrlRewritingEnabled(false);
        manager.setDeleteInvalidSessions(true);

        manager.setSessionDAO(redisSessionDao());
        manager.setSessionValidationSchedulerEnabled(true);
        manager.setDeleteInvalidSessions(true);
        manager.setSessionIdCookie(simpleCookie());
        return manager;
    }

    /**
     * 不指定名字的话，自动创建一个方法名第一个字母小写的bean
     * @Bean(name = "securityManager")
     * @return
     */
    @Bean(name="securityManager")
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(userRealm());
        manager.setCacheManager(cacheManager());
        manager.setSessionManager(configWebSessionManager());
        return manager;
    }

    @Bean(name = "cacheManagers")
    public CacheManager cacheManager(){
        return new RedisCacheManager();
    }

    @Bean
    public RedisSessionDao redisSessionDao(){
        return new RedisSessionDao();
    }

    @Bean
    public SimpleCookie simpleCookie(){
        SimpleCookie cookie = new SimpleCookie("token");
        cookie.setMaxAge(24*60*60);
        return cookie;
    }


    /**
     * 添加ShiroDialect 为了在thymeleaf里使用shiro的标签的bean
     * @return
     */
    @Bean(name = "shiroDialect")
    public ShiroDialect shiroDialect(){
        return new ShiroDialect();
    }

    @Bean(name = "myFilter")
    public CaptchaFormAuthenticationFilter captchaFormAuthenticationFilter(){
        return new CaptchaFormAuthenticationFilter();
    }

}
