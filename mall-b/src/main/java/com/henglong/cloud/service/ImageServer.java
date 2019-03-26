package com.henglong.cloud.service;

import com.henglong.cloud.dao.ImageDao;
import com.henglong.cloud.entity.Image;
import com.henglong.cloud.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Service
public class ImageServer {

    private String[] types = {".jpg", ".bmp", ".jpeg", ".png"};

    private final ImageDao imageDao;

    @Autowired
    public ImageServer(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    //添加图片
    public Json imageOne(MultipartFile file,String attribute,String describe,String other) throws IOException {
        if (!Regular.isSql(attribute))
            return API.error("参数错误");
        if (!Regular.isSql(describe))
            return API.error("参数错误");
        if (!Regular.isSql(other))
            return API.error("参数错误");
        if (file == null)
            return API.error(MessageUtils.get("file.choice"));
        if (!file.isEmpty()) {
            String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String name = file.getOriginalFilename();
            if (Arrays.asList(types).contains(type)) {
                BufferedOutputStream out = null;
                FileOutputStream outs = null;
                File fileSourcePath = new File(LoadFile.Path() + "/img/company");
                if (!fileSourcePath.exists()) {
                    fileSourcePath.mkdirs();
                }
                outs = new FileOutputStream(new File(fileSourcePath, name));
                out = new BufferedOutputStream(outs);
                out.write(file.getBytes());
                out.flush();
                outs.flush();
                out.close();
                outs.close();
                //写入用户
                Image image = imageDao.findByName(name);
                image.setName(name);
                image.setImageUrl("/image/company/"+name);
                image.setAttribute(attribute);
                image.setDescribe(describe);
                image.setOther(other);
                imageDao.save(image);
                return API.Success("/image/company/"+name);
            }else {
                return API.error(MessageUtils.get("file.format"));
            }
        }else {
            return API.error(MessageUtils.get("file.choice"));
        }
    }

    //批量上传,只上传文件，其他参数不支持
    public Json imageList(MultipartFile[] files) throws IOException {
        int ok = 0,no = 0;
        for (MultipartFile file : files){
            if (!file.isEmpty()){
                String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String name = file.getOriginalFilename();
                if (Arrays.asList(types).contains(type)) {
                    BufferedOutputStream out = null;
                    FileOutputStream outs = null;
                    File fileSourcePath = new File(LoadFile.Path() + "/img/company");
                    if (!fileSourcePath.exists()) {
                        fileSourcePath.mkdirs();
                    }
                    outs = new FileOutputStream(new File(fileSourcePath, name));
                    out = new BufferedOutputStream(outs);
                    out.write(file.getBytes());
                    out.flush();
                    outs.flush();
                    out.close();
                    outs.close();
                    //写入用户
                    Image image = imageDao.findByName(name);
                    image.setName(name);
                    image.setImageUrl("/image/company/"+name);
                    imageDao.save(image);
                    ok++;
                }else {
                    no++;
                }
            }else {
                no++;
            }
        }
        return API.Success(MessageUtils.get("image.all.1")+files.length+"，"+MessageUtils.get("image.all.2")+ok+"，"+MessageUtils.get("image.all.3")+no);
    }

    //修改不重新上传
    public Json imageUpdate(Image image){
        if (!Regular.isEntity(image)){
            return API.error("参数不合法");
        }
        if (image.getId() == null || 0 == image.getId())
            return API.error(MessageUtils.get("image.id.expire"));
        Optional<Image> optionalImage = imageDao.findById(image.getId());
        if (!optionalImage.isPresent())
            return API.error(MessageUtils.get("image.expire"));
        Image image1 = optionalImage.get();
        if (image.getName() != null && !"".equals(image.getName()))
            image1.setName(image.getName());
        if (image.getImageUrl() != null && !"".equals(image.getImageUrl()))
            image1.setImageUrl(image.getImageUrl());
        if (image.getAttribute() != null && !"".equals(image.getAttribute()))
            image1.setAttribute(image.getAttribute());
        if (image.getSpareUrl() != null && !"".equals(image.getSpareUrl()))
            image1.setSpareUrl(image.getSpareUrl());
        if (image.getDescribe() != null && !"".equals(image.getDescribe()))
            image1.setDescribe(image.getDescribe());
        if (image.getOther() != null && !"".equals(image.getOther()))
            image1.setOther(image.getOther());
        imageDao.save(image1);
        return API.Success(image1);
    }

    //不上传添加图片
    public Json ImageAdd(Image image){
        if (!Regular.isEntity(image))
            return API.error("参数不正确");
        if (image.getImageUrl() == null || "".equals(image.getImageUrl()))
            return API.error(MessageUtils.get("image.url.expire"));
        if (image.getName() == null || "".equals(image.getName()))
            return API.error(MessageUtils.get("image.name.expire"));
        imageDao.save(image);
        return API.Success(image);
    }

    //删除图片,不会删除图片，需要手动去清理服务器
    public Json ImageDelete(Integer id){
        Optional<Image> optionalImage = imageDao.findById(id);
        if (!optionalImage.isPresent())
            return API.error(MessageUtils.get("image.expire"));
        imageDao.delete(optionalImage.get());
        return API.Success(optionalImage.get());
    }

    public Json ImageAll(){
        return API.Success(imageDao.findAll());
    }

    public Json ImageName(String name){
        return API.Success(imageDao.findByName(name));
    }
}
