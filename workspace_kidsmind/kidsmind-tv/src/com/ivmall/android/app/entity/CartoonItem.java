package com.ivmall.android.app.entity;


/**
 * Created by colin on 2015/5/25.
 */
public class CartoonItem {

    private int serieId;
    private String name;
    private String imgUrl;

    private String description;
    private String title;
    private int imgId;
    private String tag;
    private boolean end;


    public int getSerieId() {
        return serieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDescription() {
        return description;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int id) {
        this.imgId = id;
    }


    public String getTag() {
        return tag;
    }

    public boolean isEnd() {
        return end;
    }
}
