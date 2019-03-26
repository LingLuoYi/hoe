package com.henglong.cloud.controller;

import com.henglong.cloud.entity.News;
import com.henglong.cloud.entity.NewsClassify;
import com.henglong.cloud.service.NewsServer;
import com.henglong.cloud.util.Json;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(allowCredentials="true")
//@RestController
//@RequestMapping("/news")
public class NewsController {

    private final NewsServer newsServer;

    @Autowired
    public NewsController(NewsServer newsServer) {
        this.newsServer = newsServer;
    }

    @RequiresPermissions(value = "admin:install")
//    @PostMapping("/news_release")
    public Json NewsAdd(News news){
        return newsServer.Release(news);
    }

    @RequiresPermissions(value = "admin:update")
//    @PostMapping("/news_update")
    public Json NewsUpdate(News news){
        return newsServer.Update(news);
    }

//    @PostMapping("/news_delete")
    public Json NewsDelete(@RequestParam Integer id){
        return  newsServer.Delete(id);
    }

//    @RequestMapping("/news_hot")
    public Json NewsHot(Integer id,Integer hos){
        return newsServer.hotNews(id,hos);
    }

//    @RequestMapping("/news_headlines")
    public Json headlines(Integer id,Integer heablines){
        return newsServer.headlines(id,heablines);
    }

//    @RequestMapping("/news_classify")
    public Json newsClassifyAdd(NewsClassify newsClassify){
        return newsServer.newsClassifyAdd(newsClassify);
    }

//    @RequestMapping("/news_classify_add")
    public Json newsClassifyAdd(Integer newsId,Integer id){
        return newsServer.newsClassifyAdd(newsId,id);
    }

    public Json newsClassifyAdd(Integer id, List<Integer> newsIdList){
        return newsServer.newsClassifyAdd(id,newsIdList);
    }


}
