package com.chen.fy.patshow.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class ShowItem extends BmobObject {

    private String title;        //标题
    private String subtitle;     //副标题
    private BmobFile photo;     //图片文件

    public ShowItem(String title, String subtitle, BmobFile photo) {
        this.title = title;
        this.subtitle = subtitle;
        this.photo = photo;
    }

    public ShowItem(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public BmobFile getPhoto() {
        return photo;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }
}
