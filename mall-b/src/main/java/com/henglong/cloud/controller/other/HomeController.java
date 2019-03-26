package com.henglong.cloud.controller.other;

import com.henglong.cloud.dao.CommodityDao;
import com.henglong.cloud.dao.ImageDao;
import com.henglong.cloud.dao.MenuDao;
import com.henglong.cloud.dao.WordsDao;
import com.henglong.cloud.entity.Commodity;
import com.henglong.cloud.entity.Image;
import com.henglong.cloud.entity.Menu;
import com.henglong.cloud.entity.Words;
import com.henglong.cloud.entity.other.About;
import com.henglong.cloud.entity.other.Help;
import com.henglong.cloud.entity.other.Home;
import com.henglong.cloud.entity.other.Wallet;
import com.henglong.cloud.service.other.AssetsDateServer;
import com.henglong.cloud.service.other.AssetsDayServer;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/hoe")
@CrossOrigin(allowCredentials="true")
public class HomeController {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final MenuDao menuDao;

    private final ImageDao imageDao;

    private final CommodityDao commodityDao;

    private final WordsDao wordsDao;

    private final AssetsDateServer assetsDateServer;

    private final AssetsDayServer assetsDayServer;

    @Autowired
    public HomeController(MenuDao menuDao, ImageDao imageDao, CommodityDao commodityDao, WordsDao wordsDao,AssetsDateServer assetsDateServer,AssetsDayServer assetsDayServer) {
        this.menuDao = menuDao;
        this.imageDao = imageDao;
        this.commodityDao = commodityDao;
        this.wordsDao = wordsDao;
        this.assetsDateServer = assetsDateServer;
        this.assetsDayServer = assetsDayServer;
    }

    @RequestMapping("/home")
    public Json Home(){
        Home home = new Home();
//        home.setNav(menuDao.findAll());
        home.setImage(imageDao.findByName("homeBanner"));
        List<Commodity> list = new ArrayList<>();
        List<Commodity> commodityList = commodityDao.findAll();
        for (int i = 0; i < 3; i++) {
            list.add(commodityList.get(i));
        }
        home.setCommodities(list);
        home.setIntroduction(wordsDao.findByTitle("首页钱包介绍"));
        home.setAbout(wordsDao.findByTitle("首页关于我们"));
        home.setCooperation(wordsDao.findByTitle("首页合作伙伴"));
//        home.setFooterText(wordsDao.findByTitle("页脚标题"));
//        List<Image> imageList = new ArrayList<>();
//
//        imageList.add(imageDao.findByName("页脚微信"));
//        imageList.add(imageDao.findByName("页脚QQ"));
//        imageList.add(imageDao.findByName("页脚微博"));
//        imageList.add(imageDao.findByName("页脚fackebook"));
//        imageList.add(imageDao.findByName("页脚Twitter"));
//        home.setFooterImage(imageList);
//        List<Menu> menusList = new ArrayList<>();
//
//        menusList.add(menuDao.findByTitle("帮助中心"));
//        menusList.add(menuDao.findByTitle("技术支持"));
//        menusList.add(menuDao.findByTitle("免责声明"));
//        home.setFooterMenu(menusList);
//
//        List<Image> imageList1 = new ArrayList<>();
//        imageList1.add(imageDao.findByName("页脚联系我们二维码1"));
//        imageList1.add(imageDao.findByName("页脚联系我们二维码2"));
//        home.setFooterImage2(imageList1);
        return API.Success(home);
    }

    @RequestMapping("/wallet")
    public Json wallet(){
        Wallet wallet = new Wallet();
        wallet.setNav(menuDao.findAll());
        wallet.setTitle(wordsDao.findByTitle("大锄冷钱包"));
        wallet.setTitle1(wordsDao.findByTitle("专业安全的一战式区块服务"));
        wallet.setImage(imageDao.findByName("钱包介绍图"));
        wallet.setDetails(wordsDao.findByTitle("钱包详情页详情"));
        return API.Success(wallet);
    }

    @RequestMapping("/about")
    public Json About(){
        About about = new About();

//        about.setNav(menuDao.findAll());
        about.setTitleImage(imageDao.findByName("关于banner"));
        about.setAdvantage(wordsDao.findByTitle("企业优势"));
        about.setLayout(wordsDao.findByTitle("平台布局"));
        about.setIntroduction(wordsDao.findByTitle("平台介绍"));

//        about.setFooterText(wordsDao.findByTitle("页脚标题"));
//        List<Image> imageList = new ArrayList<>();
//
//        imageList.add(imageDao.findByName("页脚微信"));
//        imageList.add(imageDao.findByName("页脚QQ"));
//        imageList.add(imageDao.findByName("页脚微博"));
//        imageList.add(imageDao.findByName("页脚fackebook"));
//        imageList.add(imageDao.findByName("页脚Twitter"));
//        about.setFooterImage(imageList);
//        List<Menu> menusList = new ArrayList<>();
//
//        menusList.add(menuDao.findByTitle("帮助中心"));
//        menusList.add(menuDao.findByTitle("技术支持"));
//        menusList.add(menuDao.findByTitle("免责声明"));
//        about.setFooterMenu(menusList);
//
//        List<Image> imageList1 = new ArrayList<>();
//        imageList1.add(imageDao.findByName("页脚联系我们二维码1"));
//        imageList1.add(imageDao.findByName("页脚联系我们二维码2"));
//        about.setFooterImage2(imageList1);
        return API.Success(about);
    }

    @RequestMapping("/help")
    public Json Help(){
        Help  help = new Help();

//        help.setNav(menuDao.findAll());

        help.setWordsList(wordsDao.findByClassify("账户相关"));
        help.setWordsList(wordsDao.findByClassify("入门解释"));
        help.setWordsList(wordsDao.findByClassify("云算力相关"));

//        help.setFooterText(wordsDao.findByTitle("页脚标题"));
//        List<Image> imageList = new ArrayList<>();
//
//        imageList.add(imageDao.findByName("页脚微信"));
//        imageList.add(imageDao.findByName("页脚QQ"));
//        imageList.add(imageDao.findByName("页脚微博"));
//        imageList.add(imageDao.findByName("页脚fackebook"));
//        imageList.add(imageDao.findByName("页脚Twitter"));
//        help.setFooterImage(imageList);
//        List<Menu> menusList = new ArrayList<>();
//
//        menusList.add(menuDao.findByTitle("帮助中心"));
//        menusList.add(menuDao.findByTitle("技术支持"));
//        menusList.add(menuDao.findByTitle("免责声明"));
//        help.setFooterMenu(menusList);
//
//        List<Image> imageList1 = new ArrayList<>();
//        imageList1.add(imageDao.findByName("页脚联系我们二维码1"));
//        imageList1.add(imageDao.findByName("页脚联系我们二维码2"));
//        help.setFooterImage2(imageList1);
        return API.Success(help);
    }

    @RequestMapping("/nav")
    public Json Nav(){
        return API.Success(menuDao.findAll());
    }

    @RequestMapping("/footer")
    public Json Footer(){
        footer footer = new footer();
        footer.setFooterText(wordsDao.findByTitle("页脚标题"));
        List<Image> imageList = new ArrayList<>();

        imageList.add(imageDao.findByName("页脚微信"));
        imageList.add(imageDao.findByName("页脚QQ"));
        imageList.add(imageDao.findByName("页脚微博"));
        imageList.add(imageDao.findByName("页脚fackebook"));
        imageList.add(imageDao.findByName("页脚Twitter"));
        footer.setImages(imageList);
        List<Menu> menusList = new ArrayList<>();

        menusList.add(menuDao.findByTitle("帮助中心"));
        menusList.add(menuDao.findByTitle("技术支持"));
        menusList.add(menuDao.findByTitle("免责声明"));
        footer.setMenus(menusList);

        List<Image> imageList1 = new ArrayList<>();
        imageList1.add(imageDao.findByName("页脚联系我们二维码1"));
        imageList1.add(imageDao.findByName("页脚联系我们二维码2"));
        footer.setImages1(imageList1);
        return API.Success(footer);
    }

    @RequestMapping("/assets_date")
    public Json AssetsDate(Integer index,Integer size,String startTime, String stopTime){
        return assetsDateServer.assetsDatePage(index,size,startTime,stopTime);
    }

    @RequestMapping("/assets_day")
    public Json AssetsDay(Integer index, Integer size, String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return assetsDayServer.AssetsDayPage(index,size,sdf.parse(time));
    }

    class footer{
        private Words footerText;
        private List<Image> images;
        private List<Menu> menus;
        private List<Image> images1;

        public Words getFooterText() {
            return footerText;
        }

        public void setFooterText(Words footerText) {
            this.footerText = footerText;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public List<Menu> getMenus() {
            return menus;
        }

        public void setMenus(List<Menu> menus) {
            this.menus = menus;
        }

        public List<Image> getImages1() {
            return images1;
        }

        public void setImages1(List<Image> images1) {
            this.images1 = images1;
        }
    }


}
