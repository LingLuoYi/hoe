package com.hoe.entity.other;

import com.henglong.cloud.entity.Commodity;
import com.henglong.cloud.entity.Image;
import com.henglong.cloud.entity.Menu;
import com.henglong.cloud.entity.Words;

import java.util.List;

public class Home {

    private List<Menu> nav;

    private Image image;

    private List<Commodity> commodities;

    private Words introduction;

    private Words about;

    private Words cooperation;

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

    public List<Commodity> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<Commodity> commodities) {
        this.commodities = commodities;
    }

    public Words getIntroduction() {
        return introduction;
    }

    public void setIntroduction(Words introduction) {
        this.introduction = introduction;
    }

    public Words getAbout() {
        return about;
    }

    public void setAbout(Words about) {
        this.about = about;
    }

    public Words getCooperation() {
        return cooperation;
    }

    public void setCooperation(Words cooperation) {
        this.cooperation = cooperation;
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
