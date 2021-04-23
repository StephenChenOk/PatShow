package com.chen.fy.patshow.home.share.data;

public class BaseShareResponse {
    private String msg;
    private ShareInfo response;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ShareInfo getResponse() {
        return response;
    }

    public void setResponse(ShareInfo response) {
        this.response = response;
    }
}
