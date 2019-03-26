package com.henglong.cloud.config.shiro;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import javax.servlet.ServletRequest;
import java.io.Serializable;
import java.util.Enumeration;

/**
 * @Auther: lianyc
 * @Date: 2018/8/14 15:04
 * @Description:
 */
public class ShiroSessionManager extends DefaultWebSessionManager {

    public ShiroSessionManager(){
        super();
    }

    /////////////////////////////
    //在做重复登录时，就算连redis把数据删除也还是会有Session状态，
    //猜测是在某个地方有缓存
    //这个类是为了减少与redis的交互而将Session放到Request中
    //事实证明，这里面啥都没有
    //不过shiro是有缓存的，可能是这个原因
    ///////////////////////////////


    //重写这个方法为了减少多次从redis中读取session（自定义redisSessionDao中的doReadSession方法）
    protected Session retrieveSession(SessionKey sessionKey){
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        if(sessionKey instanceof WebSessionKey){
            request = ((WebSessionKey)sessionKey).getServletRequest();
        }
        if(request != null && sessionId != null){
            Session session =  (Session) request.getAttribute(sessionId.toString());
            if(session != null){
                return session;
            }
        }
        Session session = super.retrieveSession(sessionKey);
        if(request != null && sessionId != null){
            request.setAttribute(sessionId.toString(),session);
        }
        return session;
    }
}
