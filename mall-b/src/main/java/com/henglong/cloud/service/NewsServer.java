package com.henglong.cloud.service;

import com.henglong.cloud.dao.NewsClassifyDao;
import com.henglong.cloud.dao.NewsDao;
import com.henglong.cloud.dao.UserDao;
import com.henglong.cloud.entity.News;
import com.henglong.cloud.entity.NewsClassify;
import com.henglong.cloud.entity.User;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import com.henglong.cloud.util.Regular;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NewsServer {

    private final UserDao userDao;

    private final NewsDao newsDao;

    private final NewsClassifyDao newsClassifyDao;

    @Autowired
    public NewsServer(UserDao userDao,NewsDao newsDao,NewsClassifyDao newsClassifyDao) {
        this.userDao = userDao;
        this.newsDao = newsDao;
        this.newsClassifyDao = newsClassifyDao;
    }

    //发布新闻
    public Json Release(News news){
        if (news.getTitle() == null || "".equals(news.getTitle()))
            return API.error("标题不能为空");
        if (news.getContent() == null || "".equals(news.getContent()))
            return API.error("内容不能为空");
        if (!Regular.isEntity(news))
            return API.error("参数错误");
        //获取用户
        User user = userDao.findByUserId((String) SecurityUtils.getSubject().getPrincipal());
        if (user == null)
            return API.error("获取用户失败");
        news.setUserId(user.getUserId());
        if (user.getName() != null && !"".equals(user.getName()))
            news.setUserName(user.getName());
        else if (user.getPhone() != null && !"".equals(user.getPhone()))
            news.setUserName(user.getPhone());
        else if (user.getEmail() != null && !"".equals(user.getEmail()))
            news.setUserName(user.getEmail());
        else
            return API.error("获取发布者信息失败");
        news.setPubDate(new Date());
        news.setModifyDate(new Date());
        //默认非头条
        if (news.getHeadlines() == null)
            news.setHeadlines(1);
        //默认非热点新闻
        if (news.getHotNews() == null)
            news.setHotNews(1);
        //设置空评论
        news.setComment(null);
        //0评论数
        news.setCommentNum(0);
        newsDao.save(news);
        return API.Success(news);
    }

    //修改新闻
    public Json Update(News news){
        if (news.getId() == null || 0 == news.getId())
            return API.error("没有要修改的新闻ID");
        if (!Regular.isEntity(news))
            return API.error("参数错误");
        //查询新闻
        Optional<News> optionalNews = newsDao.findById(news.getId());
        if (!optionalNews.isPresent())
            return API.error("没有查询到要修改的新闻");
        News news1 = optionalNews.get();
        if (news.getTitle() !=null && !"".equals(news.getTitle()))
            news1.setTitle(news.getTitle());
        if (news.getNewsClassifyId() !=null && !"".equals(news.getNewsClassifyId()))
            news1.setNewsClassifyId(news.getNewsClassifyId());
        if (news.getNewsClassifyName() !=null && !"".equals(news.getNewsClassifyName()))
            news1.setNewsClassifyName(news.getNewsClassifyName());
        news1.setModifyDate(new Date());
        if (news.getContent() != null && !"".equals(news.getContent()))
            news1.setComment(news.getComment());
        if (news.getAbstracts() != null && !"".equals(news.getAbstracts()))
            news1.setAbstracts(news.getAbstracts());
        if (news.getHotNews() != null)
            news1.setHotNews(news.getHotNews());
        if (news.getHeadlines() != null)
            news1.setHeadlines(news.getHeadlines());
        newsDao.save(news1);
        return API.Success(news1);
    }

    //删除新闻
    public Json Delete(Integer id){
        Optional<News> optional = newsDao.findById(id);
        if (optional.isPresent()){
            newsDao.delete(optional.get());
            return API.Success(optional.get());
        }else {
            return API.error("没有要删除的新闻");
        }
    }

    //设置热点新闻
    public Json hotNews(Integer id, Integer hot){
        Optional<News> optional = newsDao.findById(id);
        if (!optional.isPresent())
            return API.error("没有要设置的新闻");
        News news = optional.get();
        if (hot == 0)
            news.setHotNews(hot);
        else if (hot == 1)
            news.setHotNews(hot);
        else
            return API.error("请输入正确的参数");
        newsDao.save(news);
        return API.Success(news);
    }

    //设置头条
    public Json headlines(Integer id, Integer heablines){
        Optional<News> optional = newsDao.findById(id);
        if (!optional.isPresent())
            return API.error("没有要设置的新闻");
        News news = optional.get();
        if (heablines == 0)
            news.setHeadlines(heablines);
        else if (heablines == 1)
            news.setHeadlines(heablines);
        else
            return API.error("请输入正确的参数");
        newsDao.save(news);
        return API.Success(news);
    }

    //添加新闻分类
    public Json newsClassifyAdd(NewsClassify newsClassify){
        if (!Regular.isEntity(newsClassify))
            return API.error("参数错误");
        if (newsClassify.getClassifyName() == null || "".equals(newsClassify.getClassifyName()))
            return API.error("请输入分类名称");
        newsClassifyDao.save(newsClassify);
        return API.Success(newsClassify);
    }

    //分类下添加新闻,单个
    public Json newsClassifyAdd(Integer newsId,Integer id){
        //查询分类
        Optional<NewsClassify> optionalNewsClassify = newsClassifyDao.findById(id);
        if (!optionalNewsClassify.isPresent())
            return API.error("没有要添加的分类");
        //查询新闻
        Optional<News> optionalNews = newsDao.findById(newsId);
        if (!optionalNews.isPresent())
            return API.error("要添加的新闻不存在");
        NewsClassify newsClassify = optionalNewsClassify.get();
        List<News> newsList = newsClassify.getNews();
        newsList.add(optionalNews.get());
        newsClassify.setNews(newsList);
        newsClassifyDao.save(newsClassify);
        return API.Success(newsClassify);
    }

    //分类下添加新闻，批量
    public Json newsClassifyAdd(Integer id,List<Integer> newsIdList){
        Optional<NewsClassify> optionalNewsClassify = newsClassifyDao.findById(id);
        if (!optionalNewsClassify.isPresent())
            return API.error("没有要添加的分类");
        NewsClassify newsClassify = optionalNewsClassify.get();
        List<News> newsList = newsClassify.getNews();
        for (Integer newsId:newsIdList) {
            Optional<News> optionalNews = newsDao.findById(newsId);
            if (!optionalNews.isPresent())
                return API.error("要添加的新闻不存在");
            for (News news:newsList){
                if (!newsId.equals(news.getId())){
                    newsList.add(news);
                }
            }
        }
        newsClassify.setNews(newsList);
        newsClassifyDao.save(newsClassify);
        return API.Success(newsClassify);
    }

    //暂不评论
}
