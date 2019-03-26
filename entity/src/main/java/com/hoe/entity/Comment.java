package com.hoe.entity;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "news_comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//评论id

    @Column(name = "news_id")
    private Integer newsId;//新闻id

    @Column(name = "user_id",length = 50)
    private String userId;//评论用户id

    @Column(name = "user_name",length = 50)
    private String userName;//评论姓名

    @Column(name = "comment_content",columnDefinition = "MEDIUMTEXT")
    private String commentContent;//评论内容

    @Column(name = "comment_time")
    private Date commentTime;//评论时间

    public Comment(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
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

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }
}
