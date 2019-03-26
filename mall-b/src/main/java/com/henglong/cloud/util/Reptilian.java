package com.henglong.cloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 爬虫类
 */
//public class Reptilian {
//
//    /** logger */
//    private static final Logger log = LoggerFactory.getLogger(Reptilian.class);
//
//    private static final String path = LoadFile.Path();
//
//    @Scheduled(cron = "0 0 9 * * ?",fixedDelay = 300000)
//    public void Coin(){
//        try {
//            URL url = new URL("https://www.f2pool.com/help/");
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            Integer code = urlConnection.getResponseCode();
//            if (code != 200) {
//                log.error("打开网络资源失败,状态码为："+code);
//                return;
//            }
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
//            String rb, r = "";
//
//            while ((rb = bufferedReader.readLine())!= null){
//                r += rb;
//            }
//            String s = FileConfig.getSubUtilSimple(r,"<tr>" +
//                    "<th>日理论收益</th>" +
//                    "<td>(.*?) BTC 每 Thash/s</td>" +
//                    "</tr>");
//            log.info(""+s);
//            FileConfig.WriteVariable("Theoretical coin",s);
//        }catch (Exception e){
//            log.error("错误！:"+e);
//        }
//    }
//
//    /**
//     * 测试main
//     */
//    public static void main(String[] args) {
//        new Reptilian().Coin();
//    }
//}
