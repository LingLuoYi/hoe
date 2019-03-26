package com.henglong.cloud.util;


import java.text.NumberFormat;

import static java.lang.String.format;

/**
 * 金额格式化，计算后的金额四舍五入
 */
public class MoneyUtil {

    /**
     * 四舍五入
     * @param d
     * @return
     */
    public static Double formatMoney(double d) {

        return  (double)Math.round(d*100)/100;

    }

    /**
     *微信支付金额格式化，单位为分
     * @param d
     * @return
     */
    public static Integer formatWeChatMoney(Double d){

        String str = format("%.2f", (double)Math.round(d*100)/100);
        return Integer.valueOf(str.replace(".",""));

    }

    /**
     * 测试main
     */
//    public static void main(String[] args) {
//        System.out.println(MoneyUtil.formatWeChatMoney(1.895));
//    }

}
