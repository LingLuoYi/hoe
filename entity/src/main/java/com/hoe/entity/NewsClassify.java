package com.hoe.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity(name = "news_news_Classify")
public class NewsClassify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "classify_name",length = 50)
    @NotBlank(message = "分类名称不能为空")
    private String ClassifyName;//分类名称

    @Column(name = "classify_depict")
    private String ClassifyDepict;//分类描述

    @OneToMany(targetEntity = News.class,cascade = CascadeType.ALL)
    @JoinColumn(name="news_id")
    private List<News> news;//分类下的文章

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassifyName() {
        return ClassifyName;
    }

    public void setClassifyName(String classifyName) {
        ClassifyName = classifyName;
    }

    public String getClassifyDepict() {
        return ClassifyDepict;
    }

    public void setClassifyDepict(String classifyDepict) {
        ClassifyDepict = classifyDepict;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }
}
