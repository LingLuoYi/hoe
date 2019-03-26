package com.henglong.cloud.service;

import com.henglong.cloud.dao.ConfigDao;
import com.henglong.cloud.dao.RolesDao;
import com.henglong.cloud.dao.UserDao;
import com.henglong.cloud.entity.Config;
import com.henglong.cloud.entity.Roles;
import com.henglong.cloud.entity.Spread;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SignUpService {

    private static Logger log = LoggerFactory.getLogger(SignUpService.class);

    private final MySalt mySalt;

    private final UserDao userDao;

    private final RolesDao rolesDao;

    private final SpreadService spreadService;

    private final ConfigDao configDao;

    @Autowired
    public SignUpService(MySalt mySalt, UserDao userDao, RolesDao rolesDao, SpreadService spreadService, ConfigDao configDao) {
        this.mySalt = mySalt;
        this.userDao = userDao;
        this.rolesDao = rolesDao;
        this.spreadService = spreadService;
        this.configDao = configDao;
    }


    @Transactional(rollbackOn = Exception.class)
    public Json SignUp(User user, String code) {
        if (!Regular.isEntity(user))
            return API.error("参数错误");
        if (!Regular.isSql(code))
            return API.error("参数错误");
        Config config = configDao.findById(1).get();
        if (user.getPhone() != null && !"".equals(user.getPhone())){
            log.info("注册用户【" + user.getPhone() + "】！");
            String phone = user.getPhone();
            String password = user.getPassword();
            Roles roles = new Roles();
            if (password.equals(""))
                return API.error(MessageUtils.get("signup.password.no"));
            String salt = MyMd5.Md5(mySalt.Salt(config.getSaltLength()), 1024);
            //加密密码
            String pass = MyMd5.Md5(password, salt, 1024);
            //查数据库，看是否有注册
            if (userDao.findByPhone(phone) != null) {
                log.info("重复注册！！【" + phone + "】");
                return API.error(MessageUtils.get("signup.phone.no"));
            }
            //写入登录信息
            log.info("用户【"+phone+"】，写入用户登录信息");
            SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(10,10);
            String userId = ""+snowflakeIdWorker.nextId();
            user.setUserId(userId);
            user.setPhone(phone);
            user.setPassword(pass);
            user.setSalt(salt);
            //写入用户实名权限
            log.info("用户【"+phone+"】，写入实名权限");
            user.setUserStart(2);
            //写入角色
            log.info("用户【"+phone+"】，写入用户权限");
            roles.setUserId(userId);
            roles.setRole("user");
            //判断是否有推广联系人
            if (!code.equals("")) {
                Spread spread = (Spread) spreadService.SpreadInfo(code).getData();
                if (spread != null) {
                    log.info("用户【\"+phone+\"】，注册用户推荐人【" + spread.getSpreadPhone() + "】");
                    spreadService.SpreadNum(spread.getSpreadNum() + 1, spread.getSpreadPromoCode());
                    log.info("{}",spreadService.Spreads(userId,phone, code));
                }
            } else {
                log.info("{}",spreadService.Spreads(userId,phone, ""));
                log.info("用户【"+phone+"】，没有填写推荐码");
            }
            //不管用户是不是被推荐都写入推荐码
            rolesDao.save(roles);
            userDao.save(user);
            return API.Success(MessageUtils.get("signup.ok"));
        }else if (user.getEmail() != null && !"".equals(user.getEmail())){
            log.info("注册用户【" + user.getEmail() + "】！");
            String email = user.getEmail();
            String password = user.getPassword();
            Roles roles = new Roles();
            if (password.equals(""))
                return API.error(MessageUtils.get("signup.password.no"));
            String salt = MyMd5.Md5(mySalt.Salt(config.getSaltLength()), 1024);
            //加密密码
            String pass = MyMd5.Md5(password, salt, 1024);
            //查数据库，看是否有注册
            if (userDao.findByEmail(email) != null) {
                log.info("重复注册！！【" + email + "】");
                return API.error(MessageUtils.get("signup.email.no"));
            }
            //写入登录信息
            log.info("用户【"+email+"】，写入用户登录信息");
            SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(10,10);
            String userId = ""+snowflakeIdWorker.nextId();
            user.setUserId(userId);
            user.setEmail(email);
            user.setPassword(pass);
            user.setSalt(salt);
            //写入用户实名权限
            log.info("用户【"+email+"】，写入实名权限");
            user.setUserStart(2);
            //写入角色
            log.info("用户【"+email+"】，写入用户权限");
            roles.setUserId(userId);
            roles.setRole("user");
            //判断是否有推广联系人
            if (!code.equals("")) {
                Spread spread = (Spread) spreadService.SpreadInfo(code).getData();
                if (spread != null) {
                    log.info("用户【\"+phone+\"】，注册用户推荐人【" + spread.getSpreadPhone() + "】");
                    spreadService.SpreadNum(spread.getSpreadNum() + 1, spread.getSpreadPromoCode());
                    log.info("{}",spreadService.SpreadsEmail(userId,email, ""));
                }
            } else {
                log.info("{}",spreadService.SpreadsEmail(userId,email, ""));
                log.info("用户【"+email+"】，没有填写推荐码");
            }
            //不管用户是不是被推荐都写入推荐码
            rolesDao.save(roles);
            userDao.save(user);
            return API.Success(MessageUtils.get("signup.ok"));
        }else {
            return API.error(MessageUtils.get("signup.no.phone"));
        }
    }
}
