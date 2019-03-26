package com.hoe.entity.other;

import com.henglong.cloud.entity.Image;
import com.henglong.cloud.entity.Menu;
import com.henglong.cloud.entity.Words;

import java.util.List;

public class About {

    private List<Menu> nav;

    private Image titleImage;

    private Words introduction;

    private Words advantage;

    private Words layout;

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

    public Image getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(Image titleImage) {
        this.titleImage = titleImage;
    }

    public Words getIntroduction() {
        return introduction;
    }

    public void setIntroduction(Words introduction) {
        this.introduction = introduction;
    }

    public Words getAdvantage() {
        return advantage;
    }

    public void setAdvantage(Words advantage) {
        this.advantage = advantage;
    }

    public Words getLayout() {
        return layout;
    }

    public void setLayout(Words layout) {
        this.layout = layout;
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
