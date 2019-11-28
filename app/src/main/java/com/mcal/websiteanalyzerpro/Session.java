package com.mcal.websiteanalyzerpro;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Session {
    private String HTMLSourceCode;
    @SuppressLint("SimpleDateFormat")
    private String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHTMLSourceCode() {
        return HTMLSourceCode;
    }

    public void setHTMLSourceCode(String HTMLSourceCode) {
        this.HTMLSourceCode = HTMLSourceCode;
    }

    public String toString() {
        return HTMLSourceCode;
    }

    public String getTitle() {
        return url + "\n\r" + date;
    }
}