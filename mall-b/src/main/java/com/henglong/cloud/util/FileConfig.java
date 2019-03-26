package com.henglong.cloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileConfig {

    private static final String path = LoadFile.Path();

    private static final Logger log = LoggerFactory.getLogger(FileConfig.class);

    //读取配置变量
//    public static String OutputPath(String name, String value) {
//        //读取文件
//        String source = LoadFile.TemplateLoad(new File(path + "/config/cloud.config"));
//        if (source.equals("")) {
////            log.warn("配置文件异常！！");
//        }
//        //正则匹配
//        String rgex = "<" + name + ">(.*?)</" + name + ">";
//        if (getSubUtilSimple(source, rgex).equals("")) {
//            log.warn("配置参数【" + name + "】，异常！将使用默认值！");
//            value = getSubUtilSimple(source, rgex);
//        }
//        return value;
//    }

    //读取运算参数
    //返回一个Object，需要自己转对应的类型
    public static Object Variable(String name) {
        //读取文件
        String source = LoadFile.TemplateLoad(new File(path + "/number/number.operating"));
        if (source.equals(""))
            log.error("number文件获取失败，相关数据将会异常");
        String rgex = name + " = (.*?);";
        String value = getSubUtilSimple(source, rgex);
        if (value.equals("")) {
            if (name.equals("Theoretical coin"))
                log.error(name + "参数值获取失败！收益将不会自动计算");
            value = "" + 0;
        }
        return value;
    }

    //写入产数
    public static void WriteVariable(String name, String s) {
        PrintWriter fw;
        try {
            File file = new File(path + "/number/number.operating");
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "utf-8");
            BufferedReader bu = new BufferedReader(inputStreamReader);
            String r2b, r2 = "";
            while ((r2b = bu.readLine()) != null) {
                r2 += r2b;
            }
            fw = new PrintWriter(path + "/number/number.operating", "utf-8");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(r2.replaceAll(name + " = (.*?);", name + " = " + s + ";"));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error(""+e);
        }
    }


    public static void main(String[] args) {
    //自动main
//        log.info("{}",path);
//        log.info(""+new BigDecimal((String) Variable("Theoretical coin")));
        log.info("{}",new BigDecimal(0.000035).multiply(new BigDecimal(10)).multiply(new BigDecimal(15)).setScale(10,BigDecimal.ROUND_DOWN));
    }


    /**
     * 返回单个字符串，若匹配到多个的话就返回第一个
     * @param soap
     * @param rgex
     * @return
     */
    public static String getSubUtilSimple(String soap,String rgex) {
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            return m.group(1);
        }
        return "";
    }

}
