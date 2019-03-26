package com.henglong.cloud.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity(name = "Cloud_news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//序号

    @Column(name = "news_title")
    @NotBlank(message = "标题不能为空")
    private String title;//标题

    @Column(name = "user_id",length = 50)
    private String userId;//发布者id

    @Column(name = "user_Name",length = 50)
    @NotBlank(message = "发布者不能为空")
    private String userName;//发布者姓名

    @Column(name = "news_classify_id")
    private Integer newsClassifyId;//新闻分类

    @Column(name = "news_classify_name",length = 50)
    private String newsClassifyName;//分类名称

    @Column(name = "pub_Date")
    private Date pubDate;//发布时间

    @Column(name = "modify_date")
    private Date modifyDate;//最后修改时间

    @Column(name = "content",columnDefinition = "MEDIUMTEXT")
    @NotBlank(message = "新闻内容不能为空")
    private String content;//新闻内容

    @Column(name = "abstracts",columnDefinition = "TEXT")
    private String abstracts;//新闻摘要

    @Column(name = "heablines")
    private Integer headlines;//头条 0 非头条 1

    @Column(name = "hot_news")
    private Integer hotNews;//热点新闻

    @OneToOne(targetEntity = Comment.class)
    @JoinColumn(name = "comment_id",referencedColumnName = "id")
    private Comment comment;//评论

    @Column(name = "comment_num")
    private Integer commentNum;//评论次数

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getNewsClassifyId() {
        return newsClassifyId;
    }

    public void setNewsClassifyId(Integer newsClassifyId) {
        this.newsClassifyId = newsClassifyId;
    }

    public String getNewsClassifyName() {
        return newsClassifyName;
    }

    public void setNewsClassifyName(String newsClassifyName) {
        this.newsClassifyName = newsClassifyName;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public Integer getHeadlines() {
        return headlines;
    }

    public void setHeadlines(Integer headlines) {
        this.headlines = headlines;
    }

    public Integer getHotNews() {
        return hotNews;
    }

    public void setHotNews(Integer hotNews) {
        this.hotNews = hotNews;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }
}
