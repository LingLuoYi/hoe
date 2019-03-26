package com.henglong.cloud.controller;


import com.google.gson.Gson;
import com.henglong.cloud.entity.Commodity;
import com.henglong.cloud.util.HttpUtil;
import com.henglong.cloud.util.MyMd5;
import com.henglong.cloud.util.Regular;
import com.henglong.cloud.util.Time;
import org.thymeleaf.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class test {


    /**
     * 测试main
     */
    public static void main(String[] args) throws Exception {
//        System.out.println(new BigDecimal("0.002").compareTo(new BigDecimal(0.0024606000)));
//        System.out.println("441423199104018011".length());
//        String hlData = HttpUtil.doGet("http://apicloud.mob.com/exchange/code/query?key=2a62eed51b0f2&code=usdcny");
//        Map map = new Gson().fromJson(hlData,Map.class);
//        Map map1 = (Map) map.get("result");
//        System.out.println(map1.get("closePri"));
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.DATE, -1);
//        System.out.println(Time.differentDays(calendar.getTime(), new Date()));
//        System.out.println(!(new BigDecimal(0).compareTo(new BigDecimal(0)) > 0));

//        System.out.println(HttpUtil.doGet("https://pool.api.btc.com/v1/pool/status/"));
//        String salt = MyMd5.Md5("sdf;akjdshfiwefjnlashdoivdskfjnbegrhuihgjiosdjnvblhdizfkpaos;idhfgiuoadsfnhklsadh", 1024);
//        //加密密码
////        String pass = MyMd5.Md5("sdfu_&*(^$HVHT", salt, 1024);
//        String pass = MyMd5.Md5("123456", "3e71791c41a95f3308862fa59d5a2911", 1024);
//        System.out.println("salt："+salt);
//        System.out.println("pass："+pass);

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt=sdf.parse("2019-03-25 00:00:00");
//        Map<String, Object> map = new HashMap<>();
//        long times = new Date().getTime();
//        System.out.println(times);
//        map.put("pid", "bb51021ab2d34d1b89a335279c3d5a7e");
//        map.put("encrpt", MyMd5.Sha256_HMAC("bb51021ab2d34d1b89a335279c3d5a7e" + times, "3b71e176a4d041e4a71305a88c4a7815"));
//        map.put("timestap", "" + times);
//        System.out.println(HttpUtil.doPostJson("http://161.117.35.6:38080/api/project/getToken", map));0.00003992
//          System.out.println((new BigDecimal(171.36).divide(new BigDecimal(26899.3),16,BigDecimal.ROUND_DOWN)).compareTo(new BigDecimal(0.00003992*200)));
        System.out.println(Time.isSameDate(new Date(),dt));
    }

    public static void testReflect(Object model) throws Exception {
        for (Field field : model.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            System.out.println(field.getName() + ":" + field.get(model));
        }
    }
}
