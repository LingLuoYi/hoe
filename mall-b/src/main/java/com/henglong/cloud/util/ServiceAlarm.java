package com.henglong.cloud.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 钉钉机器消息人推送群
 */
public class ServiceAlarm extends AppenderBase<ILoggingEvent> {

    private static final Logger log = LoggerFactory.getLogger(ServiceAlarm.class);

    public static String WEBHOOK_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=20584521f8b0f763aadc433761b691af65f2b69cd4f4f8fa5877251a42a19292";

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if (iLoggingEvent != null && iLoggingEvent.getMessage() != null) {
            // 自定义实现System.out.println(iLoggingEvent.getMessage());
            try {
                URL url = new URL(WEBHOOK_TOKEN);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestMethod("POST"); // 设置请求方式
                connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
                connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
                connection.connect();
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
                out.append("{ \"msgtype\": \"text\", \"text\": {\"content\": \"云算力销售系统(测试)出现异常！其摘要信息为【"+iLoggingEvent.getMessage()+"】请尽快前往日志文件查看\"}}");
                out.flush();
                out.close();


                int code = connection.getResponseCode();
//                InputStream is = null;
                if (code == 200) {
                    log.info("程序运行出现异常，已通知钉钉群！");
//                    is = connection.getErrorStream();
                } else {
                    log.error("程序运行出现异常，但钉钉通知未成功，请查看");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

