package com.ivmall.android.app.entity;

import java.util.List;

/**
 * Created by colin on 2015/5/25.
 */
public class NodeInfo {

    private String userName;
    private String publishTime;
    private String noteTitle;
    private String description;
    private String imgUrl;
    private boolean isOwn;
    private List<NodeComment> comments;

    public String getUserName() {
        return userName;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public boolean isOwn() {
        return isOwn;
    }

    public List<NodeComment> getComments() {
        return comments;
    }
}
