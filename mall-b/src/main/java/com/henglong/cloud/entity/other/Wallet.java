package com.henglong.cloud.entity.other;

import com.henglong.cloud.entity.Image;
import com.henglong.cloud.entity.Menu;
import com.henglong.cloud.entity.Words;

import java.util.List;

public class Wallet {

    private List<Menu> nav;

    private Words title;

    private Words title1;

    private Image image;

    private Words details;

    public List<Menu> getNav() {
        return nav;
    }

    public void setNav(List<Menu> nav) {
        this.nav = nav;
    }

    public Words getTitle() {
        return title;
    }

    public void setTitle(Words title) {
        this.title = title;
    }

    public Words getTitle1() {
        return title1;
    }

    public void setTitle1(Words title1) {
        this.title1 = title1;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Words getDetails() {
        return details;
    }

    public void setDetails(Words details) {
        this.details = details;
    }
}
