package com.henglong.cloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉通知工具
 */
public class notice {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(notice.class);

    //审核通知机器人
    public static void examineNotice(String type,String userId){
                    try {
                URL url = new URL("https://oapi.dingtalk.com/robot/send?access_token=b44c96c6d1413903284fa219c8b814a80357371d6bbde3980abee17679a68160");
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
                if ("支付审核".equals(type)) {
                     out.append("{ \"msgtype\": \"text\", \"text\": {\"content\": \"有人提交【" + type + "】啦\n用户id【" + userId + "】\n请前去审核哦,@13751181561\"},\"at\": {\"atMobiles\": [\"13751181561\"],\"isAtAll\": false}}");
                 }else {
                     out.append("{ \"msgtype\": \"text\", \"text\": {\"content\": \"有人提交【" + type + "】啦\n用户id【" + userId + "】\n请前去审核哦\"}}");
                }
                out.flush();
                out.close();


                int code = connection.getResponseCode();
                if (code == 200) {
                    log.info("成功通知！！！");
                } else {
                    log.error("通知失败！！！！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    //下单通知机器人
    public static void purchaseNotice(String userId,String orderId,String commodityId,String type){
        try {
            URL url = new URL("https://oapi.dingtalk.com/robot/send?access_token=209eae3387dd977bfa573be4e9e0f4dfe293b0ee63097e9d6c3620e2acdd0bbc");
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
            out.append("{ \"msgtype\": \"text\", \"text\": {\"content\": \"用户id【").append(userId).append("】成功下单\n单号【").append(orderId).append("】\n商品名称【").append(commodityId).append("】\n支付方式【").append(type).append("】请知悉\"}}");
            out.flush();
            out.close();


            int code = connection.getResponseCode();
            if (code == 200) {
                log.info("成功通知！！！");
            } else {
                log.error("通知失败！！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //支付通知机器人
    public static void payNotice(String type,String money,Integer o){
        try {
            URL url = new URL("https://oapi.dingtalk.com/robot/send?access_token=f3c6e274e7be40261112021f9a6f9490b264f24846099888ecf4caed8f7a4a80");
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
            if (o == 1) {
                out.append("{ \"msgtype\": \"text\", \"text\": {\"content\": ").append(type).append("：").append(money).append("元\"}}");
            }else {
                out.append("{ \"msgtype\": \"text\", \"text\": {\"content\": 订单【").append(money).append("】\n支付方式【").append(type).append("】\n已付款，但校验失败！请前去处理\"}}");
            }
            out.flush();
            out.close();


            int code = connection.getResponseCode();
            if (code == 200) {
                log.info("成功通知！！！");
            } else {
                log.error("通知失败！！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //安全报警机器人
    public static void safeNotice(String name){
        try {
            URL url = new URL("https://oapi.dingtalk.com/robot/send?access_token=86a107478c804683b39d0f047c7da52af96138876aeb4d00d8bf07d70430d570");
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
            out.append("{ \"msgtype\": \"text\", \"text\": {\"content\": \"管理员[").append(name).append("],在提现订单审核时尝试使用SQL关键字执行操作，请尽快联系确认账号安全");
            out.flush();
            out.close();


            int code = connection.getResponseCode();
            if (code == 200) {
                log.info("成功通知！！！");
            } else {
                log.error("通知失败！！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param phone 接收手机号
     * @param type 验证码id 139490
     */
    public static String sms(String phone,String type,String code) throws UnsupportedEncodingException {
        return HttpUtil.doGet("http://api.smsbao.com/sms?u=linlouyi&p=e8010d569268a39808ea940ea83d1501&m="+phone+"&c="+ URLEncoder.encode("【比特中心】您好,您的验证码是"+code+",此验证码十分钟有效，如非本人操作，请忽略", "UTF-8"));
    }

    /**
     * @param phone 接收手机号
     * @param type 通过与否
     * @param content 备注
     */
    public static String smsPay(String phone,String type,String content) throws UnsupportedEncodingException {
        return HttpUtil.doGet("http://api.smsbao.com/sms?u=linlouyi&p=e8010d569268a39808ea940ea83d1501&m="+phone+"&c="+ URLEncoder.encode("【比特中心】您好,您的付款请求："+type+","+content+"如非本人操作，请忽略", "UTF-8"));
    }

    /**
     *
     * @param phone 手机号
     * @param num 内容
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String smsReflect(String phone,String num) throws UnsupportedEncodingException {
        return HttpUtil.doGet("http://api.smsbao.com/sms?u=linlouyi&p=e8010d569268a39808ea940ea83d1501&m="+phone+"&c="+ URLEncoder.encode("【比特中心】你好,您已成功提交提现请求，提现数量:"+num+",如非本人操作，请尽快联系客服！", "UTF-8"));
    }


    /**
     *
     * @param phone
     * @param assetsId
     * @param type
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String smsReflectE(String phone,String assetsId,String type) throws UnsupportedEncodingException {
        return HttpUtil.doGet("http://api.smsbao.com/sms?u=linlouyi&p=e8010d569268a39808ea940ea83d1501&m="+phone+"&c="+ URLEncoder.encode("【比特中心】你好,您提交提现请求:"+type+"，资产号:"+assetsId+",如非本人操作，请尽快联系客服！", "UTF-8"));
    }


    public static String smsReflectP(String phone,String assetsId,String type,String num) throws UnsupportedEncodingException {
        return HttpUtil.doGet("http://api.smsbao.com/sms?u=linlouyi&p=e8010d569268a39808ea940ea83d1501&m="+phone+"&c="+ URLEncoder.encode("【比特中心】你好,您提交提现请求:"+type+"，资产号:"+assetsId+",支付货币数量:"+num+",如非本人操作，请尽快联系客服！", "UTF-8"));
    }


}
