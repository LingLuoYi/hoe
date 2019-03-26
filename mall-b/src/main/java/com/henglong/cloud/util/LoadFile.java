package com.henglong.cloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;

public class LoadFile {

    private static final Logger log = LoggerFactory.getLogger(LoadFile.class);

    //读取文件加载为字符串
    public static String TemplateLoad(File file){
        StringBuilder result = new StringBuilder();
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
        }catch(Exception e){
//            log.warn("文件读取异常！");
        }finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    log.warn("文件读取流关闭异常！");
                }
            }
        }
        return result.toString();
    }

    public static String Path() {
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            log.error("本地路径加载出错!");
        }
        if(!path.exists()) path = new File("");
        File upload = new File(path.getAbsolutePath(),"resource/");
        if(!upload.exists()) upload.mkdirs();
        return upload.getAbsolutePath();
    }
}
