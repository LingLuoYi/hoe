package com.henglong.cloud.controller;

import com.henglong.cloud.entity.Image;
import com.henglong.cloud.service.ImageServer;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.LoadFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/image")
public class ImageController {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    private final ImageServer imageServer;

    @Autowired
    public ImageController(ImageServer imageServer) {
        this.imageServer = imageServer;
    }

    @RequestMapping("/company/{name}")
    public void image(HttpServletResponse response,@PathVariable("name")String name){
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(LoadFile.Path()+"/img/company/"+name);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.error("显示图片发生了异常");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("图片显示类，关闭流出现了异常",e);
                }
            }
        }
    }

    @PostMapping("/upload_one")
    public Json ImageOne(@RequestParam("image_file") MultipartFile file, String attribute, String describe, String other) throws IOException {
        return imageServer.imageOne(file,attribute,describe,other);
    }

    @PostMapping("/upload_list")
    public Json ImageList(@RequestParam("image_files") MultipartFile[] files) throws IOException {
        return imageServer.imageList(files);
    }

    @RequestMapping("/image_update")
    public Json imageUpdate(Image image){
        return imageServer.imageUpdate(image);
    }

    @RequestMapping("/image_add")
    public Json imageAdd(Image image){
        return imageServer.ImageAdd(image);
    }

    @RequestMapping("/image_delete")
    public Json imageDelete(Integer id){
        return imageServer.ImageDelete(id);
    }

    @RequestMapping("/image_all")
    public Json imageAll(){
        return imageServer.ImageAll();
    }

    @RequestMapping("/image_name")
    public Json imageName(String name){
        return imageServer.ImageName(name);
    }
}
