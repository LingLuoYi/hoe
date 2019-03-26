package com.henglong.cloud.util;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则匹配类
 */
public class Regular {

    private static final String REGX = "!|！|@|◎|#|＃|(\\$)|￥|%|％|(\\^)|……|(\\&)|※|(\\*)|×|(\\()|（|(\\))|）|_|——|(\\+)|＋|(\\|)|§ ";

    //验证手机号
    public static Boolean isPhone(String mobile) {
        if (mobile == null){
            return false;
        }
        if (mobile.length() != 11) {
            return false;
        } else {
            /**
             * 移动号段正则表达式
             */
            String pat1 = "^((13[4-9])|(147)|(15[0-2,7-9])|(178)|(18[2-4,7-8]))\\d{8}|(1705)\\d{7}$";
            /**
             * 联通号段正则表达式
             */
            String pat2 = "^((13[0-2])|(145)|(15[5-6])|(176)|(18[5,6]))\\d{8}|(1709)\\d{7}$";
            /**
             * 电信号段正则表达式
             */
            String pat3 = "^((133)|(153)|(177)|(18[0,1,9])|(149))\\d{8}$";
            /**
             * 虚拟运营商正则表达式
             */
            String pat4 = "^((170))\\d{8}|(1718)|(1719)\\d{7}$";

            Pattern pattern1 = Pattern.compile(pat1);
            Matcher match1 = pattern1.matcher(mobile);
            boolean isMatch1 = match1.matches();
            if (isMatch1) {
                return true;
            }
            Pattern pattern2 = Pattern.compile(pat2);
            Matcher match2 = pattern2.matcher(mobile);
            boolean isMatch2 = match2.matches();
            if (isMatch2) {
                return true;
            }
            Pattern pattern3 = Pattern.compile(pat3);
            Matcher match3 = pattern3.matcher(mobile);
            boolean isMatch3 = match3.matches();
            if (isMatch3) {
                return true;
            }
            Pattern pattern4 = Pattern.compile(pat4);
            Matcher match4 = pattern4.matcher(mobile);
            boolean isMatch4 = match4.matches();
            if (isMatch4) {
                return true;
            }
            return false;
        }
    }

    //验证小数
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    //验证整数
    public static boolean isNumeric(String str) {
        if (str != null) {
            Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
            Matcher isNum = pattern.matcher(str);
            return isNum.matches();
        }
        return true;
    }

    //验证全英文
    public static boolean isString(String str){
      return str.matches("[a-zA-Z]+");
    }

    //验证邮箱
    public static boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

    //验证字符串中包含sql关键字
    public static Boolean isSql(String sInput) {
        if (sInput != null) {
            sInput = sInput.toUpperCase();
            return !sInput.contains("DELETE") && !sInput.contains("ASCII") && !sInput.contains("UPDATE") && !sInput.contains("SELECT")
                    && !sInput.contains("'") && !sInput.contains("SUBSTR(") && !sInput.contains("COUNT(") && !sInput.contains(" OR ")
                    && !sInput.contains(" AND ") && !sInput.contains("DROP") && !sInput.contains("EXECUTE") && !sInput.contains("EXEC")
                    && !sInput.contains("TRUNCATE") && !sInput.contains("INTO") && !sInput.contains("DECLARE") && !sInput.contains("MASTER")
                    && !sInput.contains("FROM");
        }
        return true;
    }

    //验证字符串是否合法
    public static Boolean isStr(String sInput) {

        if (sInput == null || sInput.trim().length() == 0) {
            return false;
        }
        sInput = sInput.trim();
        Pattern compile = Pattern.compile(REGX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(sInput);
        return matcher.find();
    }

    //验证类参数是否合法
    public static Boolean isEntity(Object model){
        for (Field field : model.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (Regular.isSql(String.valueOf(field.get(model)))){
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
