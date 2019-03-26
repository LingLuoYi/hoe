package com.henglong.cloud.entity.other;

import com.henglong.cloud.entity.Image;
import com.henglong.cloud.entity.Menu;
import com.henglong.cloud.entity.Words;

import java.util.List;

public class Help {

    private List<Menu> nav;

    private Image image;

    private List<Words> wordsList;

    private List<Words> wordsList1;

    private List<Words> wordsList2;

    private Words footerText;

    private List<Image> footerImage;

    private List<Menu> footerMenu;

    private List<Image> footerImage2;

    public List<Menu> getNav() {
        return nav;
    }

    public void setNav(List<Menu> nav) {
        this.nav = nav;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<Words> getWordsList() {
        return wordsList;
    }

    public void setWordsList(List<Words> wordsList) {
        this.wordsList = wordsList;
    }

    public List<Words> getWordsList1() {
        return wordsList1;
    }

    public void setWordsList1(List<Words> wordsList1) {
        this.wordsList1 = wordsList1;
    }

    public List<Words> getWordsList2() {
        return wordsList2;
    }

    public void setWordsList2(List<Words> wordsList2) {
        this.wordsList2 = wordsList2;
    }

    public Words getFooterText() {
        return footerText;
    }

    public void setFooterText(Words footerText) {
        this.footerText = footerText;
    }

    public List<Image> getFooterImage() {
        return footerImage;
    }

    public void setFooterImage(List<Image> footerImage) {
        this.footerImage = footerImage;
    }

    public List<Menu> getFooterMenu() {
        return footerMenu;
    }

    public void setFooterMenu(List<Menu> footerMenu) {
        this.footerMenu = footerMenu;
    }

    public List<Image> getFooterImage2() {
        return footerImage2;
    }

    public void setFooterImage2(List<Image> footerImage2) {
        this.footerImage2 = footerImage2;
    }
}
