package com.henglong.cloud.controller;

import com.henglong.cloud.entity.Commodity;
import com.henglong.cloud.service.CommodityService;
import com.henglong.cloud.service.FileService;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.CodeConstant;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.LoadFile;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@CrossOrigin(allowCredentials="true")
@RestController
@RequestMapping("/commodity")
public class CommodityController {

    private static final Logger log= LoggerFactory.getLogger(CommodityController.class);

    private final CommodityService commodityService;

    private final FileService fileService;

    @Autowired
    public CommodityController(CommodityService commodityService, FileService fileService) {
        this.commodityService = commodityService;
        this.fileService = fileService;
    }

    @RequestMapping("/commodity_info_all")
    public Json CommodityInfoAll(String id){
        try {
            return commodityService.CommodityAllRead(id);
        }catch (Exception e){
            log.info("出现未知错误！！"+e);
            return API.error(CodeConstant.ERR_SYSTEM,e.getMessage());
        }
    }

    @RequestMapping("/commodity_info_id")
    public Json CommodityInfoId(@RequestParam("id") String id) throws Exception {
        return commodityService.CommodityOneForId(id);
    }

    @RequestMapping("/commodity_info_name")
    public Json CommodityInfoName(@RequestParam("name") String name) throws Exception {
        return commodityService.CommodityForName(name);
    }

    @RequestMapping("/commodity_info_type")
    public Json CommodityInfoType(@RequestParam("type") String type) throws Exception {
        return commodityService.CommodityForType(type);
    }

    @RequestMapping("/commodity_info_page")
    public Json CommodityInfoPage(Integer page,Integer limit,String name,String id) throws Exception {
        return commodityService.CommodityPage(page,limit,name,id);
    }

    @RequestMapping("/commodity_info_state")
    public Json CommodityInfoState(Integer state) throws Exception {
        return commodityService.CommodityState(state);
    }

    @RequiresPermissions("storehouse:install")
    @RequestMapping("/file_img")
    public Json File(@RequestParam(value = "commodity_img",required = false) MultipartFile file , @RequestParam("id") String id) throws Exception {
        return fileService.CommodityFile(file,id);
    }

    /*图片加载连接*/
    @RequestMapping("/commodity_img/{name}")
    public void PayImg(HttpServletResponse response,@PathVariable("name")String name){
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(LoadFile.Path()+"/img/Commodity/"+name);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.warn("显示图片发生了异常",e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.warn("图片显示类，关闭流出现了异常",e);
                }
            }
        }
    }


    /**
     * 添加商品方法
     * @param commodity
     * @return
     */
    @RequiresPermissions(value = {"storehouse:install","admin:install"},logical = Logical.OR)
    @PostMapping("/commodity_add")
    public Json CommodityAdd(Commodity commodity) throws Exception {
        log.info("添加商品【"+commodity.getCommodityName()+"】");
            return commodityService.CommodityAdd(commodity);
    }

    /**
     * 修改商品方法
     * @param commodity
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = {"storehouse:update","admin:update"},logical= Logical.OR)
    @PostMapping("/commodity_update")
    public Json CommodityUpdate(@Valid Commodity commodity) throws Exception {
        return commodityService.CommodityAdd(commodity) ;
    }



    /**
     * 购买信息
     * **重要**
     * @param id
     * @param num
     * @return
     */
    @RequiresPermissions(value = {"user:install","admin:install"},logical = Logical.OR)
    @PostMapping("/commodity_purchase")
    public Json CommodityPurchase(@RequestParam("id") String id, @RequestParam("num") String num,Integer addressId,Integer maintain,Integer term) {
        try {
            return commodityService.CommodityPurchase(id, num, addressId,maintain,term,null);
        }catch (Exception e){
            return API.error(CodeConstant.ERR_SYSTEM,e.getMessage());
        }
    }
}
