package com.henglong.cloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(Time.class);


    public static String TimePuls(String time,Integer T) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date dt=sdf.parse(time);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.DATE,T);
        Date dt1=rightNow.getTime();
        return sdf.format(dt1);
    }

    /**
     * 计算天
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2) //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0) //闰年
                {
                    timeDistance += 366;
                }
                else //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return (timeDistance + (day2-day1)) ;
        }
        else //不同年
        {
            return ((day2-day1));
        }
    }

    /**
     * 计算分钟
     * @param d1 //当前时间
     * @param d2 //历史时间
     * @return
     */
    public static long diffHours(Date d1,Date d2){
        try{
//            Date d1 = df.parse("2004-03-26 13:31:40");
//            Date d2 = df.parse("2004-01-02 11:30:24");
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
//            long days = diff / (1000 * 60 * 60 * 24);
//            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
//            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            return diff / (1000 * 60 * 60);
        }catch (Exception e){
            return -1;
        }

    }



    /**
     *
     * N为分钟数
     * 前面一个为当前参数，后面一个为历史参数，过期则true，没过期则false
     * @param time 当前时间
     * @param now 过期时间
     * @param n 期限
     * @return
     */
    public static boolean belongDate(Date time, Date now, int n) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(now);//把当前时间赋给日历
        calendar.add(Calendar.MINUTE, n);
        Date before7days = calendar.getTime();   //得到n前的时间
        if (before7days.getTime() <= time.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * N为天数
     * 前面一个为当前参数，后面一个为历史参数，过期则true，没过期则false
     * @param time 当前时间
     * @param now 过期时间
     * @param n 期限
     * @return
     */
    public static boolean belongDateDay(Date time, Date now, int n) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(now);//把当前时间赋给日历
        calendar.add(Calendar.DATE, n);
        Date before7days = calendar.getTime();   //得到n前的时间
        if (before7days.getTime() <= time.getTime()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断两个时间是否是同一天
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }


}
