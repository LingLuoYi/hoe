package com.henglong.cloud.util;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MySalt {
    public String Salt(Integer n){
        String val = "";

        Random random = new Random();
        for(int i = 0; i < n; i++)
        {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

            if("char".equalsIgnoreCase(charOrNum)) // 字符串
            {
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
                val += (char) (choice + random.nextInt(26));
            }
            else if("num".equalsIgnoreCase(charOrNum)) // 数字
            {
                val += String.valueOf(random.nextInt(10));
            }
        }

        return val;
    }


}
