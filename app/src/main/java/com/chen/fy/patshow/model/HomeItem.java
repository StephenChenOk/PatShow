package com.chen.fy.patshow.model;

public class HomeItem {

    private int imageID;
    private String content;

    public HomeItem(int imageID, String content) {
        this.imageID = imageID;
        this.content = content;
    }

    public int getImageID() {
        return imageID;
    }

    public String getContent() {
        return content;
    }
}
