package com.hoe.entity;

import javax.persistence.*;

@Entity(name = "Cloud_words")
public class Words {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "words_title",length = 50)
    private String title;

    @Column(name = "words_classify",length = 50)
    private String classify;

    @Column(name = "words_purpose",length = 50)
    private String purpose;

    @Column(name = "words_content",columnDefinition = "MEDIUMTEXT")
    private String content;

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

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
